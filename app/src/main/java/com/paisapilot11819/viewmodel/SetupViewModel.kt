package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.CategoryEntity
import com.paisapilot11819.data.local.entity.MonthPlanEntity
import com.paisapilot11819.data.repository.CategoryRepository
import com.paisapilot11819.data.repository.MonthRepository
import com.paisapilot11819.data.repository.RecurringRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class CategoryDraft(
    val categoryId: Long? = null,
    val name: String,
    val budgetPaise: Long
)

data class SetupUiState(
    val yearMonth: String = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")),
    val openingBalancePaise: Long = 0L,
    val dailyLimitPaise: Long? = null,
    val weeklyLimitPaise: Long? = null,
    val categories: List<CategoryDraft> = emptyList(),
    val availableCategories: List<CategoryEntity> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isReadyToNavigate: Long? = null
) {
    val totalAllocatedPaise: Long get() = categories.sumOf { it.budgetPaise }
    val unallocatedPaise: Long get() = openingBalancePaise - totalAllocatedPaise
    val isAllocationValid: Boolean get() = totalAllocatedPaise <= openingBalancePaise && openingBalancePaise > 0
}

class SetupViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val monthRepo = MonthRepository(db)
    private val categoryRepo = CategoryRepository(db)
    private val recurringRepo = RecurringRepository(db)

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            checkCurrentMonth()
            categoryRepo.getAllCategories().collect { list ->
                _uiState.update { it.copy(availableCategories = list) }
            }
        }
    }

    private suspend fun checkCurrentMonth() {
        val currentYm = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val existing = monthRepo.getByYearMonthOnce(currentYm)
        if (existing != null && existing.isSetUp) {
            // Process any due recurring deductions
            recurringRepo.processRecurringDeductions(currentYm, existing.id)
            _uiState.update { it.copy(isReadyToNavigate = existing.id) }
        } else {
            _uiState.update { it.copy(yearMonth = currentYm) }
        }
    }

    fun setYearMonth(ym: String) {
        _uiState.update { it.copy(yearMonth = ym) }
        viewModelScope.launch {
            val existing = monthRepo.getByYearMonthOnce(ym)
            if (existing != null && existing.isSetUp) {
                _uiState.update { it.copy(isReadyToNavigate = existing.id) }
            }
        }
    }

    fun setOpeningBalance(paise: Long) {
        _uiState.update { it.copy(openingBalancePaise = paise, errorMessage = null) }
    }

    fun setDailyLimit(paise: Long?) {
        _uiState.update { it.copy(dailyLimitPaise = paise) }
    }

    fun setWeeklyLimit(paise: Long?) {
        _uiState.update { it.copy(weeklyLimitPaise = paise) }
    }

    fun addCategoryAllocation(name: String, budgetPaise: Long) {
        val current = _uiState.value
        val newTotal = current.totalAllocatedPaise + budgetPaise
        if (newTotal > current.openingBalancePaise) {
            val remainingCanAllocate = current.openingBalancePaise - current.totalAllocatedPaise
            _uiState.update {
                it.copy(errorMessage = "Cannot allocate! Maximum remaining unallocated is ₹${remainingCanAllocate / 100.0}")
            }
            return
        }

        val updated = current.categories + CategoryDraft(name = name.trim(), budgetPaise = budgetPaise)
        _uiState.update { it.copy(categories = updated, errorMessage = null) }
    }

    fun removeCategoryAllocation(index: Int) {
        val updated = _uiState.value.categories.toMutableList()
        if (index in updated.indices) {
            updated.removeAt(index)
            _uiState.update { it.copy(categories = updated, errorMessage = null) }
        }
    }

    fun saveMonthPlan(onComplete: (Long) -> Unit) {
        val state = _uiState.value
        if (state.openingBalancePaise <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a positive opening balance") }
            return
        }
        if (!state.isAllocationValid) {
            _uiState.update { it.copy(errorMessage = "Allocated budgets exceed opening balance!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val existing = monthRepo.getByYearMonthOnce(state.yearMonth)
            val monthId = if (existing != null) {
                val updated = existing.copy(
                    openingBalancePaise = state.openingBalancePaise,
                    dailyLimitPaise = state.dailyLimitPaise,
                    weeklyLimitPaise = state.weeklyLimitPaise,
                    isSetUp = true
                )
                monthRepo.updateMonth(updated)
                existing.id
            } else {
                monthRepo.createMonth(
                    yearMonth = state.yearMonth,
                    openingBalancePaise = state.openingBalancePaise,
                    dailyLimitPaise = state.dailyLimitPaise,
                    weeklyLimitPaise = state.weeklyLimitPaise
                )
            }

            // Save categories and budgets
            for (draft in state.categories) {
                val categoryId = if (draft.categoryId != null) {
                    draft.categoryId
                } else {
                    categoryRepo.createCategory(draft.name)
                }
                categoryRepo.setBudget(monthId, categoryId, draft.budgetPaise)
            }

            // Process any recurring rules for this month
            recurringRepo.processRecurringDeductions(state.yearMonth, monthId)

            _uiState.update { it.copy(isSaving = false, isReadyToNavigate = monthId) }
            onComplete(monthId)
        }
    }
}
