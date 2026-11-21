package com.paisapilot11819.ui.screens.recurring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.data.local.entity.RecurringRuleEntity
import com.paisapilot11819.ui.components.ConfirmDialog
import com.paisapilot11819.ui.components.CurrencyUtils
import com.paisapilot11819.ui.components.EmptyStateView
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.RecurringViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    onAddRule: () -> Unit,
    onEditRule: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: RecurringViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var ruleToDelete by remember { mutableStateOf<RecurringRuleEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Bills") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRule,
                containerColor = AccentGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = AppShapes.ButtonCorner
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recurring Bill")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.rules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Default.Autorenew,
                    title = "No Recurring Rules",
                    subtitle = "Automate regular bills like Rent, Subscriptions, or Utilities. They are charged once a month automatically."
                )
            }
        } else {
            val catMap = remember(uiState.categories) { uiState.categories.associateBy { it.id } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(AppDimens.itemSpacing),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.CardCorner,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Recurring deductions post once per month on their due day. Per your rules, they deduct from monthly funds and category budget, but are excluded from daily/weekly limits.",
                            style = AppTypography.bodySmall,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                items(uiState.rules, key = { it.id }) { rule ->
                    val catName = rule.categoryId?.let { catMap[it]?.name } ?: "Uncategorized"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.CardCorner,
                        colors = CardDefaults.cardColors(
                            containerColor = if (rule.isActive) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rule.label,
                                    style = AppTypography.titleMedium,
                                    color = if (rule.isActive) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$catName • Due day ${rule.dueDay} of month",
                                    style = AppTypography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = CurrencyUtils.formatPaiseCompact(rule.amountPaise),
                                    style = AppTypography.titleMedium,
                                    color = if (rule.isActive) WarningRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = rule.isActive,
                                onCheckedChange = { viewModel.toggleRuleActive(rule) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentGreen,
                                    checkedTrackColor = AccentGreen.copy(alpha = 0.4f)
                                )
                            )

                            IconButton(onClick = { ruleToDelete = rule }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WarningRed)
                            }
                        }
                    }
                }
            }
        }
    }

    ruleToDelete?.let { rule ->
        ConfirmDialog(
            title = "Delete Recurring Rule?",
            message = "Stop future charges for '${rule.label}' (${CurrencyUtils.formatPaiseCompact(rule.amountPaise)})? Previously posted deductions in historical months will be preserved.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteRule(rule)
                ruleToDelete = null
            },
            onDismiss = { ruleToDelete = null }
        )
    }
}
