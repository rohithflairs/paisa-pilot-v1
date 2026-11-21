package com.paisapilot11819.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.ui.components.*
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    monthPlanId: Long,
    onAddExpense: () -> Unit,
    onAddMoney: () -> Unit,
    onViewActivityLog: () -> Unit,
    onNavigateToRecurring: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(app, monthPlanId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Paisa Pilot", style = AppTypography.headlineMedium)
                        Text(
                            text = uiState.monthPlan?.yearMonth ?: "",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToRecurring) {
                        Icon(Icons.Default.Autorenew, contentDescription = "Recurring Bills")
                    }
                    IconButton(onClick = onViewActivityLog) {
                        Icon(Icons.Default.List, contentDescription = "Activity Log")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile & History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(AppDimens.itemSpacing)
            ) {
                // Reallocation warning banner if needed
                if (uiState.isReallocationNeeded) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = AppShapes.CardCorner,
                            colors = CardDefaults.cardColors(containerColor = WarningRed.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = WarningRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Reallocation Needed!",
                                        style = AppTypography.titleMedium,
                                        color = WarningRed
                                    )
                                    Text(
                                        "Category budgets exceed available funds by ${CurrencyUtils.formatPaiseCompact(-uiState.unallocatedBudgetPaise)}.",
                                        style = AppTypography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Remaining Balance Card
                item {
                    BalanceCard(
                        title = "Remaining Balance",
                        balancePaise = uiState.mainBalanceRemainingPaise,
                        subtitle = "Net Spent: ${CurrencyUtils.formatPaiseCompact(uiState.netSpendingPaise)}"
                    )
                }

                // Action Buttons: Add Money & Add Expense side by side
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onAddMoney,
                            modifier = Modifier
                                .weight(1f)
                                .height(AppDimens.buttonHeight),
                            shape = AppShapes.ButtonCorner,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Money")
                        }

                        Button(
                            onClick = onAddExpense,
                            modifier = Modifier
                                .weight(1f)
                                .height(AppDimens.buttonHeight),
                            shape = AppShapes.ButtonCorner,
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Spend")
                        }
                    }
                }

                // Monthly Overview Numbers Breakdown Card
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
                                Column {
                                    Text("Opening Balance", style = AppTypography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(CurrencyUtils.formatPaiseCompact(uiState.openingBalancePaise), style = AppTypography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Money Added", style = AppTypography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("+${CurrencyUtils.formatPaiseCompact(uiState.addMoneyTotalPaise)}", style = AppTypography.titleMedium, color = SuccessGreen)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Total Expenses", style = AppTypography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("-${CurrencyUtils.formatPaiseCompact(uiState.totalExpensesPaise)}", style = AppTypography.titleMedium, color = WarningRed)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Refunds", style = AppTypography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("+${CurrencyUtils.formatPaiseCompact(uiState.totalRefundsPaise)}", style = AppTypography.titleMedium, color = RefundBlue)
                                }
                            }
                        }
                    }
                }

                // Daily & Weekly Spending Limits
                item {
                    Text("Period Limits", style = AppTypography.titleMedium)
                }

                item {
                    LimitIndicator(
                        label = "Today's Spending",
                        spentPaise = uiState.todaySpentPaise,
                        limitPaise = uiState.dailyLimitPaise
                    )
                }

                item {
                    LimitIndicator(
                        label = "This Week's Spending",
                        spentPaise = uiState.thisWeekSpentPaise,
                        limitPaise = uiState.weeklyLimitPaise
                    )
                }

                // Category Spending Bars
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category Budgets", style = AppTypography.titleMedium)
                        Text(
                            "Unallocated: ${CurrencyUtils.formatPaiseCompact(uiState.unallocatedBudgetPaise)}",
                            style = AppTypography.bodySmall,
                            color = if (uiState.unallocatedBudgetPaise < 0) WarningRed else AccentGreen
                        )
                    }
                }

                if (uiState.categoryProgressList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = AppShapes.CardCorner
                        ) {
                            Text(
                                "No categories allocated. Edit plan to add categories.",
                                modifier = Modifier.padding(16.dp),
                                style = AppTypography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(uiState.categoryProgressList) { cat ->
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
                }

                // Quick Activity Log Link
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onViewActivityLog,
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.ButtonCorner
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Complete Activity Log")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

