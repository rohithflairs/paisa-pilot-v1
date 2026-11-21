package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.*
import com.paisapilot11819.data.repository.CategoryRepository
import com.paisapilot11819.data.repository.TransactionRepository
import com.paisapilot11819.ui.components.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActivityItem(
    val id: Long,
    val type: TransactionType,
    val title: String,
    val amountPaise: Long,
    val date: String,
    val subtitle: String? = null,
    val categoryId: Long? = null,
    val isRecurring: Boolean = false,
    val rawExpense: ExpenseEntity? = null,
    val rawAddMoney: AddMoneyEntity? = null,
    val rawRefund: RefundEntity? = null
)

data class ActivityLogUiState(
    val items: List<ActivityItem> = emptyList(),
    val filteredItems: List<ActivityItem> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedType: TransactionType? = null,
    val isLoading: Boolean = true
)

class ActivityLogViewModel(
    application: Application,
    private val monthPlanId: Long
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val txRepo = TransactionRepository(db)
    private val categoryRepo = CategoryRepository(db)

    private val _uiState = MutableStateFlow(ActivityLogUiState())
    val uiState: StateFlow<ActivityLogUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                txRepo.getExpensesForMonth(monthPlanId),
                txRepo.getAddMoneyForMonth(monthPlanId),
                txRepo.getRefundsForMonth(monthPlanId),
                categoryRepo.getAllIncludingArchived()
            ) { expenses, addMoneyList, refunds, categories ->
                val catMap = categories.associateBy { it.id }

                val expenseItems = expenses.map { exp ->
                    val catName = exp.categoryId?.let { catMap[it]?.name } ?: "Uncategorized"
                    ActivityItem(
                        id = exp.id,
                        type = TransactionType.EXPENSE,
                        title = catName,
                        amountPaise = exp.amountPaise,
                        date = exp.expenseDate,
                        subtitle = exp.note,
                        categoryId = exp.categoryId,
                        isRecurring = exp.isRecurring,
                        rawExpense = exp
                    )
                }

                val addMoneyItems = addMoneyList.map { am ->
                    ActivityItem(
                        id = am.id,
                        type = TransactionType.ADD_MONEY,
                        title = am.source ?: "Top-up",
                        amountPaise = am.amountPaise,
                        date = am.dateReceived,
                        subtitle = am.note,
                        rawAddMoney = am
                    )
                }

                val refundItems = refunds.map { ref ->
                    val linkedExp = expenses.find { it.id == ref.originalExpenseId }
                    val catName = linkedExp?.categoryId?.let { catMap[it]?.name }
                    ActivityItem(
                        id = ref.id,
                        type = TransactionType.REFUND,
                        title = if (catName != null) "Refund: $catName" else "Refund",
                        amountPaise = ref.amountPaise,
                        date = ref.dateReceived,
                        subtitle = ref.note,
                        categoryId = linkedExp?.categoryId,
                        rawRefund = ref
                    )
                }

                val all = (expenseItems + addMoneyItems + refundItems)
                    .sortedByDescending { it.date }

                _uiState.update { current ->
                    val filtered = applyFilter(all, current.selectedCategoryId, current.selectedType)
                    current.copy(
                        items = all,
                        filteredItems = filtered,
                        categories = categories,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun filterByCategory(categoryId: Long?) {
        _uiState.update { current ->
            val newCatId = if (current.selectedCategoryId == categoryId) null else categoryId
            val filtered = applyFilter(current.items, newCatId, current.selectedType)
            current.copy(selectedCategoryId = newCatId, filteredItems = filtered)
        }
    }

    fun filterByType(type: TransactionType?) {
        _uiState.update { current ->
            val newType = if (current.selectedType == type) null else type
            val filtered = applyFilter(current.items, current.selectedCategoryId, newType)
            current.copy(selectedType = newType, filteredItems = filtered)
        }
    }

    private fun applyFilter(
        items: List<ActivityItem>,
        catId: Long?,
        type: TransactionType?
    ): List<ActivityItem> {
        return items.filter { item ->
            val matchesCat = catId == null || item.categoryId == catId
            val matchesType = type == null || item.type == type
            matchesCat && matchesType
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            txRepo.deleteExpense(expense)
        }
    }

    fun deleteAddMoney(addMoney: AddMoneyEntity) {
        viewModelScope.launch {
            txRepo.deleteAddMoney(addMoney)
        }
    }

    fun deleteRefund(refund: RefundEntity) {
        viewModelScope.launch {
            txRepo.deleteRefund(refund)
        }
    }
}

