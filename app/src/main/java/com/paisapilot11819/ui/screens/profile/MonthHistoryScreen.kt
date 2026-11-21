package com.paisapilot11819.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.ui.components.*
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.ActivityLogViewModel
import com.paisapilot11819.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthHistoryScreen(
    monthPlanId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val dashViewModel: DashboardViewModel = viewModel(
        key = "history_dash_$monthPlanId",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(app, monthPlanId) as T
            }
        }
    )

    val logViewModel: ActivityLogViewModel = viewModel(
        key = "history_log_$monthPlanId",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActivityLogViewModel(app, monthPlanId) as T
            }
        }
    )

    val dashState by dashViewModel.uiState.collectAsState()
    val logState by logViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${dashState.monthPlan?.yearMonth ?: "Month"} · Summary") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = AppDimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.itemSpacing),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Balance card for that month
            item {
                BalanceCard(
                    title = "Closing Balance",
                    balancePaise = dashState.mainBalanceRemainingPaise,
                    subtitle = "Net Spending: ${CurrencyUtils.formatPaiseCompact(dashState.netSpendingPaise)}"
                )
            }

            // Summary breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.CardCorner,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Opening Balance", style = AppTypography.bodySmall)
                            Text(CurrencyUtils.formatPaiseCompact(dashState.openingBalancePaise), style = AppTypography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Money Added", style = AppTypography.bodySmall)
                            Text("+${CurrencyUtils.formatPaiseCompact(dashState.addMoneyTotalPaise)}", style = AppTypography.titleMedium, color = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Expenses", style = AppTypography.bodySmall)
                            Text("-${CurrencyUtils.formatPaiseCompact(dashState.totalExpensesPaise)}", style = AppTypography.titleMedium, color = WarningRed)
                        }
                        if (dashState.totalRefundsPaise > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Refunds", style = AppTypography.bodySmall)
                                Text("+${CurrencyUtils.formatPaiseCompact(dashState.totalRefundsPaise)}", style = AppTypography.titleMedium, color = RefundBlue)
                            }
                        }
                    }
                }
            }

            // Category breakdown header
            item {
                Text("Category Spending", style = AppTypography.titleLarge)
            }

            items(dashState.categoryProgressList) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.CardCorner,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(14.dp)) {
                        BudgetProgressBar(
                            categoryName = cat.categoryName,
                            spentPaise = cat.netSpentPaise,
                            budgetPaise = cat.budgetPaise
                        )
                    }
                }
            }

            // Activity Log header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dated Log (${logState.items.size} entries)", style = AppTypography.titleLarge)
            }

            items(logState.items) { item ->
                TransactionItem(
                    type = item.type,
                    title = item.title,
                    amountPaise = item.amountPaise,
                    date = item.date,
                    subtitle = item.subtitle,
                    isRecurring = item.isRecurring
                )
            }
        }
    }
}

