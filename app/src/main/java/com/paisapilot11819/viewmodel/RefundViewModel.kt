package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.ExpenseEntity
import com.paisapilot11819.data.local.entity.RefundEntity
import com.paisapilot11819.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class RefundUiState(
    val expenseId: Long = 0L,
    val originalExpense: ExpenseEntity? = null,
    val previousRefundsTotalPaise: Long = 0L,
    val refundAmountPaise: Long = 0L,
    val dateReceived: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val maxRefundAllowedPaise: Long
        get() = (originalExpense?.amountPaise ?: 0L) - previousRefundsTotalPaise
}

class RefundViewModel(
    application: Application,
    private val expenseId: Long
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val txRepo = TransactionRepository(db)

    private val _uiState = MutableStateFlow(RefundUiState(expenseId = expenseId))
    val uiState: StateFlow<RefundUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val exp = txRepo.getExpenseByIdOnce(expenseId)
            if (exp != null) {
                txRepo.getRefundsByExpenseId(expenseId).collect { refunds ->
                    val totalPrior = refunds.sumOf { it.amountPaise }
                    _uiState.update {
                        it.copy(
                            originalExpense = exp,
                            previousRefundsTotalPaise = totalPrior,
                            refundAmountPaise = exp.amountPaise - totalPrior // defaults to remaining unrefunded
                        )
                    }
                }
            }
        }
    }

    fun setAmount(paise: Long) {
        _uiState.update { it.copy(refundAmountPaise = paise, errorMessage = null) }
    }

    fun setDate(date: String) {
        _uiState.update { it.copy(dateReceived = date) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveRefund(onSuccess: () -> Unit) {
        val state = _uiState.value
        val exp = state.originalExpense ?: return
        if (state.refundAmountPaise <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter an amount greater than 0") }
            return
        }
        if (state.refundAmountPaise > state.maxRefundAllowedPaise) {
            _uiState.update {
                it.copy(errorMessage = "Refund cannot exceed remaining purchase amount of ₹${state.maxRefundAllowedPaise / 100.0}")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val entity = RefundEntity(
                originalExpenseId = exp.id,
                monthPlanId = exp.monthPlanId,
                amountPaise = state.refundAmountPaise,
                dateReceived = state.dateReceived,
                note = state.note.takeIf { it.isNotBlank() }
            )
            txRepo.addRefund(entity)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}

