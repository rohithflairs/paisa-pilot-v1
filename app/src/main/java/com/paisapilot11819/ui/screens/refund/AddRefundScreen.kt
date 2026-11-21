package com.paisapilot11819.ui.screens.refund

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.paisapilot11819.viewmodel.RefundViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRefundScreen(
    expenseId: Long,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: RefundViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RefundViewModel(app, expenseId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.refundAmountPaise) {
        if (uiState.refundAmountPaise > 0 && amountText.isEmpty()) {
            amountText = (uiState.refundAmountPaise / 100.0).toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Refund") },
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
            // Original Expense Info Card
            uiState.originalExpense?.let { exp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.CardCorner,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Original Expense Details", style = AppTypography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total Purchase: ${CurrencyUtils.formatPaiseCompact(exp.amountPaise)}", style = AppTypography.bodyMedium)
                        if (uiState.previousRefundsTotalPaise > 0) {
                            Text("Already Refunded: ${CurrencyUtils.formatPaiseCompact(uiState.previousRefundsTotalPaise)}", style = AppTypography.bodySmall, color = RefundBlue)
                        }
                        Text("Max Refundable: ${CurrencyUtils.formatPaiseCompact(uiState.maxRefundAllowedPaise)}", style = AppTypography.bodySmall, color = AccentGreen)
                        Text("Date: ${exp.expenseDate}", style = AppTypography.bodySmall)
                        if (!exp.note.isNullOrBlank()) {
                            Text("Note: ${exp.note}", style = AppTypography.bodySmall)
                        }
                    }
                }
            }

            // Explanatory Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.CardCorner,
                colors = CardDefaults.cardColors(containerColor = RefundBlue.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "A refund credits your cash balance once and reduces net spending in the purchase category.",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(14.dp)
                )
            }

            // Refund Amount Input Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input
                    val paise = CurrencyUtils.rupeesToPaise(input) ?: 0L
                    viewModel.setAmount(paise)
                },
                label = { Text("Refund Amount (₹)") },
                placeholder = { Text("0.00") },
                textStyle = AppTypography.amountInput,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date picker
            DatePickerField(
                label = "Date Refund Received",
                selectedDate = uiState.dateReceived,
                onDateSelected = { viewModel.setDate(it) }
            )

            // Note input
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.setNote(it) },
                label = { Text("Note (Optional)") },
                placeholder = { Text("e.g. Return item to store") },
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
                onClick = { viewModel.saveRefund(onSaved) },
                enabled = uiState.refundAmountPaise > 0 && uiState.refundAmountPaise <= uiState.maxRefundAllowedPaise && !uiState.isSaving,
                shape = AppShapes.ButtonCorner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimens.buttonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = RefundBlue)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Refund", style = AppTypography.titleMedium)
                }
            }
        }
    }
}
