package com.paisapilot11819.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.ui.components.CurrencyUtils
import com.paisapilot11819.ui.components.DatePickerField
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    monthPlanId: Long,
    expenseId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: ExpenseViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpenseViewModel(app, monthPlanId, expenseId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.amountPaise) {
        if (uiState.amountPaise > 0 && amountText.isEmpty()) {
            amountText = (uiState.amountPaise / 100.0).toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId != null) "Edit Expense" else "Record Spend") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AppDimens.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Input Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input
                    val paise = CurrencyUtils.rupeesToPaise(input) ?: 0L
                    viewModel.setAmount(paise)
                },
                label = { Text("Amount Spent (₹)") },
                placeholder = { Text("0.00") },
                textStyle = AppTypography.amountInput,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Selection
            Text("Category", style = AppTypography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    AssistChip(
                        onClick = { showNewCategoryDialog = true },
                        label = { Text("+ New") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        shape = AppShapes.ChipCorner
                    )
                }

                items(uiState.categories) { category ->
                    val isSelected = uiState.selectedCategoryId == category.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(category.id) },
                        label = { Text(category.name) },
                        shape = AppShapes.ChipCorner,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGreen,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Date picker
            DatePickerField(
                label = "Date of Expense",
                selectedDate = uiState.expenseDate,
                onDateSelected = { viewModel.setDate(it) }
            )

            // Note input
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.setNote(it) },
                label = { Text("Note (Optional)") },
                placeholder = { Text("e.g. Lunch with team, Groceries") },
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth()
            )

            // Error display
            uiState.errorMessage?.let { error ->
                Text(error, color = WarningRed, style = AppTypography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = { viewModel.saveExpense(onSaved) },
                enabled = uiState.amountPaise > 0 && !uiState.isSaving,
                shape = AppShapes.ButtonCorner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimens.buttonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (expenseId != null) "Update Expense" else "Save Expense", style = AppTypography.titleMedium)
                }
            }
        }
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text("Create New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text("Category Name") },
                    shape = AppShapes.InputFieldCorner,
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            viewModel.createCategory(newCategoryInput)
                            newCategoryInput = ""
                            showNewCategoryDialog = false
                        }
                    },
                    shape = AppShapes.ButtonCorner
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
