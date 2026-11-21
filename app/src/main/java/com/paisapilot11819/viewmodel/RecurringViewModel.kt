package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.CategoryEntity
import com.paisapilot11819.data.local.entity.RecurringRuleEntity
import com.paisapilot11819.data.repository.CategoryRepository
import com.paisapilot11819.data.repository.RecurringRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class RecurringUiState(
    val rules: List<RecurringRuleEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true
)

class RecurringViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val recurringRepo = RecurringRepository(db)
    private val categoryRepo = CategoryRepository(db)

    private val _uiState = MutableStateFlow(RecurringUiState())
    val uiState: StateFlow<RecurringUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                recurringRepo.getAllRules(),
                categoryRepo.getAllIncludingArchived()
            ) { rules, categories ->
                RecurringUiState(
                    rules = rules,
                    categories = categories,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun toggleRuleActive(rule: RecurringRuleEntity) {
        viewModelScope.launch {
            recurringRepo.updateRule(rule.copy(isActive = !rule.isActive))
        }
    }

    fun deleteRule(rule: RecurringRuleEntity) {
        viewModelScope.launch {
            recurringRepo.deleteRule(rule)
        }
    }
}

data class AddRecurringRuleUiState(
    val ruleId: Long? = null,
    val label: String = "",
    val amountPaise: Long = 0L,
    val selectedCategoryId: Long? = null,
    val startYearMonth: String = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")),
    val dueDay: Int = 1,
    val categories: List<CategoryEntity> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class AddRecurringRuleViewModel(
    application: Application,
    private val ruleId: Long?
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val recurringRepo = RecurringRepository(db)
    private val categoryRepo = CategoryRepository(db)

    private val _uiState = MutableStateFlow(AddRecurringRuleUiState(ruleId = ruleId))
    val uiState: StateFlow<AddRecurringRuleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepo.getAllCategories().collect { list ->
                _uiState.update { it.copy(categories = list) }
            }
        }

        if (ruleId != null) {
            viewModelScope.launch {
                val rule = recurringRepo.getRuleByIdOnce(ruleId)
                if (rule != null) {
                    _uiState.update {
                        it.copy(
                            label = rule.label,
                            amountPaise = rule.amountPaise,
                            selectedCategoryId = rule.categoryId,
                            startYearMonth = rule.startYearMonth,
                            dueDay = rule.dueDay
                        )
                    }
                }
            }
        }
    }

    fun setLabel(label: String) { _uiState.update { it.copy(label = label) } }
    fun setAmount(paise: Long) { _uiState.update { it.copy(amountPaise = paise, errorMessage = null) } }
    fun setCategory(id: Long?) { _uiState.update { it.copy(selectedCategoryId = id) } }
    fun setStartMonth(ym: String) { _uiState.update { it.copy(startYearMonth = ym) } }
    fun setDueDay(day: Int) { _uiState.update { it.copy(dueDay = day.coerceIn(1, 31)) } }

    fun saveRule(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.label.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a label for this recurring bill") }
            return
        }
        if (state.amountPaise <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter an amount greater than 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val rule = RecurringRuleEntity(
                id = state.ruleId ?: 0L,
                label = state.label.trim(),
                amountPaise = state.amountPaise,
                categoryId = state.selectedCategoryId,
                startYearMonth = state.startYearMonth,
                dueDay = state.dueDay,
                isActive = true
            )

            if (state.ruleId != null) {
                recurringRepo.updateRule(rule)
            } else {
                recurringRepo.createRule(rule)
            }

            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}

