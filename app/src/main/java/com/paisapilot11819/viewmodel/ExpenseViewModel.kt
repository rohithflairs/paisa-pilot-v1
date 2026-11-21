package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.CategoryEntity
import com.paisapilot11819.data.local.entity.ExpenseEntity
import com.paisapilot11819.data.repository.CategoryRepository
import com.paisapilot11819.data.repository.MonthRepository
import com.paisapilot11819.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ExpenseUiState(
    val monthPlanId: Long = 0L,
    val expenseId: Long? = null,
    val amountPaise: Long = 0L,
    val selectedCategoryId: Long? = null,
    val expenseDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val note: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ExpenseViewModel(
    application: Application,
    private val initialMonthPlanId: Long,
    private val expenseId: Long?
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val txRepo = TransactionRepository(db)
    private val categoryRepo = CategoryRepository(db)
    private val monthRepo = MonthRepository(db)

    private val _uiState = MutableStateFlow(ExpenseUiState(monthPlanId = initialMonthPlanId, expenseId = expenseId))
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepo.getAllCategories().collect { list ->
                _uiState.update { it.copy(categories = list) }
            }
        }

        if (expenseId != null) {
            viewModelScope.launch {
                val expense = txRepo.getExpenseByIdOnce(expenseId)
                if (expense != null) {
                    _uiState.update {
                        it.copy(
                            monthPlanId = expense.monthPlanId,
                            amountPaise = expense.amountPaise,
                            selectedCategoryId = expense.categoryId,
                            expenseDate = expense.expenseDate,
                            note = expense.note ?: ""
                        )
                    }
                }
            }
        }
    }

    fun setAmount(paise: Long) {
        _uiState.update { it.copy(amountPaise = paise, errorMessage = null) }
    }

    fun setCategory(categoryId: Long?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setDate(date: String) {
        _uiState.update { it.copy(expenseDate = date) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            val id = categoryRepo.createCategory(name.trim())
            _uiState.update { it.copy(selectedCategoryId = id) }
        }
    }

    fun saveExpense(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.amountPaise <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount greater than 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val existingExpense = if (state.expenseId != null) txRepo.getExpenseByIdOnce(state.expenseId) else null

            val expense = ExpenseEntity(
                id = state.expenseId ?: 0L,
                monthPlanId = state.monthPlanId,
                categoryId = state.selectedCategoryId,
                amountPaise = state.amountPaise,
                expenseDate = state.expenseDate,
                note = state.note.takeIf { it.isNotBlank() },
                isRecurring = existingExpense?.isRecurring ?: false,
                recurringRuleId = existingExpense?.recurringRuleId
            )

            if (state.expenseId != null) {
                txRepo.updateExpense(expense)
            } else {
                txRepo.addExpense(expense)
            }

            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
