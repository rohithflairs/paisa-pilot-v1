package com.paisapilot11819.ui.screens.addmoney

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.paisapilot11819.viewmodel.AddMoneyViewModel
import com.paisapilot11819.viewmodel.PredefinedSources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneyScreen(
    monthPlanId: Long,
    addMoneyId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: AddMoneyViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddMoneyViewModel(app, monthPlanId, addMoneyId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.amountPaise) {
        if (uiState.amountPaise > 0 && amountText.isEmpty()) {
            amountText = (uiState.amountPaise / 100.0).toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (addMoneyId != null) "Edit Top-Up" else "Add Money") },
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
            // Explanatory card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.CardCorner,
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "Adding money increases your unallocated funds and total available balance. It does not automatically raise category limits.",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(14.dp)
                )
            }

            // Amount Input Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    amountText = input
                    val paise = CurrencyUtils.rupeesToPaise(input) ?: 0L
                    viewModel.setAmount(paise)
                },
                label = { Text("Amount (₹)") },
                placeholder = { Text("0.00") },
                textStyle = AppTypography.amountInput,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Source Selector
            Text("Source", style = AppTypography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PredefinedSources) { src ->
                    val isSelected = uiState.source == src
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSource(src) },
                        label = { Text(src) },
                        shape = AppShapes.ChipCorner,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessGreen,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Custom source text if needed
            OutlinedTextField(
                value = uiState.source,
                onValueChange = { viewModel.setSource(it) },
                label = { Text("Custom Source / Label (Optional)") },
                placeholder = { Text("e.g. Bonus, Sold Old Phone") },
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date picker
            DatePickerField(
                label = "Date Received",
                selectedDate = uiState.dateReceived,
                onDateSelected = { viewModel.setDate(it) }
            )

            // Note input
            OutlinedTextField(
                value = uiState.note,
                onValueChange = { viewModel.setNote(it) },
                label = { Text("Note (Optional)") },
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
                onClick = { viewModel.saveAddMoney(onSaved) },
                enabled = uiState.amountPaise > 0 && !uiState.isSaving,
                shape = AppShapes.ButtonCorner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimens.buttonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (addMoneyId != null) "Update Top-Up" else "Add to Balance", style = AppTypography.titleMedium)
                }
            }
        }
    }
}

