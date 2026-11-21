package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.local.entity.AddMoneyEntity
import com.paisapilot11819.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AddMoneyUiState(
    val monthPlanId: Long = 0L,
    val addMoneyId: Long? = null,
    val amountPaise: Long = 0L,
    val dateReceived: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val source: String = "",
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

val PredefinedSources = listOf("Salary", "Freelance", "Gift", "Investment", "Other")

class AddMoneyViewModel(
    application: Application,
    private val initialMonthPlanId: Long,
    private val addMoneyId: Long?
) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val txRepo = TransactionRepository(db)

    private val _uiState = MutableStateFlow(
        AddMoneyUiState(monthPlanId = initialMonthPlanId, addMoneyId = addMoneyId)
    )
    val uiState: StateFlow<AddMoneyUiState> = _uiState.asStateFlow()

    init {
        if (addMoneyId != null) {
            viewModelScope.launch {
                val item = txRepo.getAddMoneyByIdOnce(addMoneyId)
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            monthPlanId = item.monthPlanId,
                            amountPaise = item.amountPaise,
                            dateReceived = item.dateReceived,
                            source = item.source ?: "",
                            note = item.note ?: ""
                        )
                    }
                }
            }
        }
    }

    fun setAmount(paise: Long) {
        _uiState.update { it.copy(amountPaise = paise, errorMessage = null) }
    }

    fun setDate(date: String) {
        _uiState.update { it.copy(dateReceived = date) }
    }

    fun setSource(source: String) {
        _uiState.update { it.copy(source = source) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveAddMoney(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.amountPaise <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount greater than 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val entity = AddMoneyEntity(
                id = state.addMoneyId ?: 0L,
                monthPlanId = state.monthPlanId,
                amountPaise = state.amountPaise,
                dateReceived = state.dateReceived,
                source = state.source.takeIf { it.isNotBlank() },
                note = state.note.takeIf { it.isNotBlank() }
            )

            if (state.addMoneyId != null) {
                txRepo.updateAddMoney(entity)
            } else {
                txRepo.addMoney(entity)
            }

            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}

