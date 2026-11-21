package com.paisapilot11819.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.data.backup.BackupManager
import com.paisapilot11819.data.backup.ImportResult
import com.paisapilot11819.data.local.entity.MonthPlanEntity
import com.paisapilot11819.data.repository.MonthRepository
import com.paisapilot11819.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MonthSummary(
    val monthPlan: MonthPlanEntity,
    val openingBalancePaise: Long,
    val addMoneyTotalPaise: Long,
    val expensesTotalPaise: Long,
    val refundsTotalPaise: Long
) {
    val netSpendingPaise: Long get() = expensesTotalPaise - refundsTotalPaise
    val remainingBalancePaise: Long get() = openingBalancePaise + addMoneyTotalPaise - expensesTotalPaise + refundsTotalPaise
    val hasZeroSpending: Boolean get() = expensesTotalPaise == 0L
}

data class ProfileUiState(
    val monthSummaries: List<MonthSummary> = emptyList(),
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportJson: String? = null,
    val importResult: ImportResult? = null,
    val isLoading: Boolean = true
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PaisaPilotApp).database
    private val monthRepo = MonthRepository(db)
    private val txRepo = TransactionRepository(db)
    private val backupManager = BackupManager(db)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            monthRepo.getAllMonths().collect { months ->
                val summaries = months.map { plan ->
                    val added = txRepo.getTotalAddMoneyForMonthOnce(plan.id) ?: 0L
                    val spent = txRepo.getTotalExpensesForMonthOnce(plan.id) ?: 0L
                    val refunded = db.refundDao().getTotalForMonth(plan.id).first() ?: 0L

                    MonthSummary(
                        monthPlan = plan,
                        openingBalancePaise = plan.openingBalancePaise,
                        addMoneyTotalPaise = added,
                        expensesTotalPaise = spent,
                        refundsTotalPaise = refunded
                    )
                }
                _uiState.update { it.copy(monthSummaries = summaries, isLoading = false) }
            }
        }
    }

    fun exportData(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val json = backupManager.exportToJson()
            _uiState.update { it.copy(isExporting = false, exportJson = json) }
            onSuccess(json)
        }
    }

    fun importData(json: String, onComplete: (ImportResult) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            val result = backupManager.importFromJson(json)
            _uiState.update { it.copy(isImporting = false, importResult = result) }
            if (result.success) {
                loadHistory()
            }
            onComplete(result)
        }
    }
}

