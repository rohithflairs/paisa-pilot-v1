package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.*
import com.paisapilot11819.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class CategoryProgress(
    val categoryId: Long,
    val categoryName: String,
    val budgetPaise: Long,
    val spentPaise: Long,
    val refundedPaise: Long
) {
    val netSpentPaise: Long get() = spentPaise - refundedPaise
    val remainingPaise: Long get() = budgetPaise - netSpentPaise
    val isOverBudget: Boolean get() = remainingPaise < 0
    val isLimitReached: Boolean get() = remainingPaise == 0L && budgetPaise > 0
}

data class DailySpendPoint(
    val dayOfMonth: Int,
    val dateString: String,
    val amountPaise: Long
)

data class DashboardUiState(
    val monthPlan: MonthPlanEntity? = null,
    val openingBalancePaise: Long = 0L,
    val addMoneyTotalPaise: Long = 0L,
    val totalExpensesPaise: Long = 0L,
    val totalRefundsPaise: Long = 0L,
    val categoryProgressList: List<CategoryProgress> = emptyList(),
    val todaySpentPaise: Long = 0L,
    val thisWeekSpentPaise: Long = 0L,
    val dailySpendPoints: List<DailySpendPoint> = emptyList(),
    val recentTransactions: List<ExpenseEntity> = emptyList(),
    val isLoading: Boolean = true
) {
    // ── Strict calculations from Section 3 ──
    val monthlyFundsPaise: Long get() = openingBalancePaise + addMoneyTotalPaise
    val totalCategoryBudgetsPaise: Long get() = categoryProgressList.sumOf { it.budgetPaise }
    val unallocatedBudgetPaise: Long get() = monthlyFundsPaise - totalCategoryBudgetsPaise
    val isReallocationNeeded: Boolean get() = unallocatedBudgetPaise < 0
    val netSpendingPaise: Long get() = totalExpensesPaise - totalRefundsPaise
    val mainBalanceRemainingPaise: Long get() = openingBalancePaise + addMoneyTotalPaise - totalExpensesPaise + totalRefundsPaise

    val dailyLimitPaise: Long? get() = monthPlan?.dailyLimitPaise
    val weeklyLimitPaise: Long? get() = monthPlan?.weeklyLimitPaise

    val dailyRemainingPaise: Long? get() = dailyLimitPaise?.let { it - todaySpentPaise }
    val weeklyRemainingPaise: Long? get() = weeklyLimitPaise?.let { it - thisWeekSpentPaise }
}

class DashboardViewModel(
    application: Application,
    private val monthPlanId: Long
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val monthRepo = MonthRepository(db)
    private val categoryRepo = CategoryRepository(db)
    private val txRepo = TransactionRepository(db)
    private val recurringRepo = RecurringRepository(db)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            monthRepo.getById(monthPlanId).filterNotNull().collect { plan ->
                _uiState.update { it.copy(monthPlan = plan, openingBalancePaise = plan.openingBalancePaise) }

                // Auto-process any pending recurring deductions for this month
                recurringRepo.processRecurringDeductions(plan.yearMonth, plan.id)

                // Combine all data streams for this month (5 streams max for native combine overload)
                combine(
                    txRepo.getAddMoneyForMonth(plan.id),
                    txRepo.getExpensesForMonth(plan.id),
                    txRepo.getRefundsForMonth(plan.id),
                    categoryRepo.getBudgetsForMonth(plan.id),
                    categoryRepo.getAllIncludingArchived()
                ) { addMoneyList, expenses, refunds, budgets, categories ->
                    val catMap = categories.associateBy { it.id }

                    val addMoneyTotal = addMoneyList.sumOf { it.amountPaise }
                    val totalExp = expenses.sumOf { it.amountPaise }
                    val totalRef = refunds.sumOf { it.amountPaise }

                    // Compute category progress
                    val progressList = budgets.map { budget ->
                        val cat = catMap[budget.categoryId]
                        val catExpenses = expenses.filter { it.categoryId == budget.categoryId }
                            .sumOf { it.amountPaise }
                        val catRefunds = refunds.filter { ref ->
                            val exp = expenses.find { it.id == ref.originalExpenseId }
                            exp?.categoryId == budget.categoryId
                        }.sumOf { it.amountPaise }

                        CategoryProgress(
                            categoryId = budget.categoryId,
                            categoryName = cat?.name ?: "Unknown",
                            budgetPaise = budget.budgetPaise,
                            spentPaise = catExpenses,
                            refundedPaise = catRefunds
                        )
                    }

                    // Compute today's and this week's spending (excluding recurring per requirement)
                    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val todaySpent = expenses
                        .filter { it.expenseDate == today && !it.isRecurring }
                        .sumOf { it.amountPaise }

                    val now = LocalDate.now()
                    val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)

                    val thisWeekSpent = expenses
                        .filter { it.expenseDate in monday..sunday && !it.isRecurring }
                        .sumOf { it.amountPaise }

                    // Daily spend chart data across the month
                    val ym = YearMonth.parse(plan.yearMonth)
                    val daysInMonth = ym.lengthOfMonth()
                    val dailyPoints = (1..daysInMonth).map { day ->
                        val dateStr = LocalDate.of(ym.year, ym.month, day)
                            .format(DateTimeFormatter.ISO_LOCAL_DATE)
                        val dayTotal = expenses.filter { it.expenseDate == dateStr }
                            .sumOf { it.amountPaise }
                        DailySpendPoint(
                            dayOfMonth = day,
                            dateString = dateStr,
                            amountPaise = dayTotal
                        )
                    }

                    DashboardUiState(
                        monthPlan = plan,
                        openingBalancePaise = plan.openingBalancePaise,
                        addMoneyTotalPaise = addMoneyTotal,
                        totalExpensesPaise = totalExp,
                        totalRefundsPaise = totalRef,
                        categoryProgressList = progressList,
                        todaySpentPaise = todaySpent,
                        thisWeekSpentPaise = thisWeekSpent,
                        dailySpendPoints = dailyPoints,
                        recentTransactions = expenses.take(5),
                        isLoading = false
                    )
                }.collect { combinedState ->
                    _uiState.value = combinedState
                }
            }
        }
    }
}

