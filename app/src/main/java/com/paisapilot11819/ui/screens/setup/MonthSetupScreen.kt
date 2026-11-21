package com.paisapilot11819.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.ui.components.CurrencyUtils
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSetupScreen(
    onMonthReady: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SetupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var openingBalanceText by remember { mutableStateOf("") }
    var dailyLimitText by remember { mutableStateOf("") }
    var weeklyLimitText by remember { mutableStateOf("") }

    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryBudgetText by remember { mutableStateOf("") }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isReadyToNavigate) {
        uiState.isReadyToNavigate?.let { monthId ->
            onMonthReady(monthId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paisa Pilot · Setup Month", style = AppTypography.headlineMedium) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "History & Settings")
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
            verticalArrangement = Arrangement.spacedBy(AppDimens.itemSpacing)
        ) {
            item {
                Text(
                    text = "Set up plan for ${uiState.yearMonth}",
                    style = AppTypography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your starting balance and allocate budgets across categories.",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Opening Balance Input
            item {
                OutlinedTextField(
                    value = openingBalanceText,
                    onValueChange = { input ->
                        openingBalanceText = input
                        val paise = CurrencyUtils.rupeesToPaise(input) ?: 0L
                        viewModel.setOpeningBalance(paise)
                    },
                    label = { Text("Opening Balance (₹)") },
                    placeholder = { Text("e.g. 10000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = AppShapes.InputFieldCorner,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Unallocated preview card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.CardCorner,
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.unallocatedPaise < 0) WarningRed.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Allocated:", style = AppTypography.bodyMedium)
                            Text(CurrencyUtils.formatPaiseCompact(uiState.totalAllocatedPaise), style = AppTypography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Unallocated Funds:", style = AppTypography.bodyMedium)
                            Text(
                                CurrencyUtils.formatPaiseCompact(uiState.unallocatedPaise),
                                style = AppTypography.titleMedium,
                                color = if (uiState.unallocatedPaise < 0) WarningRed else AccentGreen
                            )
                        }
                    }
                }
            }

            // Optional Daily & Weekly limits
            item {
                Text("Spending Limits (Optional)", style = AppTypography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dailyLimitText,
                        onValueChange = {
                            dailyLimitText = it
                            val paise = CurrencyUtils.rupeesToPaise(it)
                            viewModel.setDailyLimit(paise)
                        },
                        label = { Text("Daily Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = AppShapes.InputFieldCorner,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = weeklyLimitText,
                        onValueChange = {
                            weeklyLimitText = it
                            val paise = CurrencyUtils.rupeesToPaise(it)
                            viewModel.setWeeklyLimit(paise)
                        },
                        label = { Text("Weekly Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = AppShapes.InputFieldCorner,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Category allocations header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Category Budgets", style = AppTypography.titleMedium)
                    FilledTonalButton(
                        onClick = { showAddCategoryDialog = true },
                        shape = AppShapes.ButtonCorner
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Category")
                    }
                }
            }

            // Category list
            if (uiState.categories.isEmpty()) {
                item {
                    Text(
                        text = "No categories added yet. Tap 'Add Category' to allocate your budget.",
                        style = AppTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(uiState.categories) { index, cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.CardCorner,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(cat.name, style = AppTypography.titleMedium)
                                Text(
                                    CurrencyUtils.formatPaiseCompact(cat.budgetPaise),
                                    style = AppTypography.bodyMedium,
                                    color = AccentGreen
                                )
                            }
                            IconButton(onClick = { viewModel.removeCategoryAllocation(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = WarningRed)
                            }
                        }
                    }
                }
            }

            // Error display
            uiState.errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        style = AppTypography.bodyMedium,
                        color = WarningRed
                    )
                }
            }

            // Submit button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.saveMonthPlan(onMonthReady) },
                    enabled = uiState.openingBalancePaise > 0 && uiState.isAllocationValid && !uiState.isSaving,
                    shape = AppShapes.ButtonCorner,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimens.buttonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Finish Setup & Open Dashboard", style = AppTypography.titleMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Dialog to add category
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Category Budget") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g. Food, Travel, Rent") },
                        shape = AppShapes.InputFieldCorner,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newCategoryBudgetText,
                        onValueChange = { newCategoryBudgetText = it },
                        label = { Text("Budget Amount (₹)") },
                        placeholder = { Text("e.g. 3000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = AppShapes.InputFieldCorner,
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val paise = CurrencyUtils.rupeesToPaise(newCategoryBudgetText) ?: 0L
                        if (newCategoryName.isNotBlank() && paise > 0) {
                            viewModel.addCategoryAllocation(newCategoryName, paise)
                            newCategoryName = ""
                            newCategoryBudgetText = ""
                            showAddCategoryDialog = false
                        }
                    },
                    shape = AppShapes.ButtonCorner
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
