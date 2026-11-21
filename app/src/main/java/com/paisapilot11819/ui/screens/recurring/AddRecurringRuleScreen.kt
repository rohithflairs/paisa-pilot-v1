package com.paisapilot11819.ui.screens.recurring

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.ui.components.CurrencyUtils
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.AddRecurringRuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringRuleScreen(
    ruleId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: AddRecurringRuleViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddRecurringRuleViewModel(app, ruleId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var dueDayText by remember { mutableStateOf("1") }

    LaunchedEffect(uiState.amountPaise) {
        if (uiState.amountPaise > 0 && amountText.isEmpty()) {
            amountText = (uiState.amountPaise / 100.0).toString()
        }
    }

    LaunchedEffect(uiState.dueDay) {
        dueDayText = uiState.dueDay.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ruleId != null) "Edit Recurring Bill" else "New Recurring Bill") },
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
            OutlinedTextField(
                value = uiState.label,
                onValueChange = { viewModel.setLabel(it) },
                label = { Text("Bill Label / Name") },
                placeholder = { Text("e.g. Rent, Netflix, Internet Bill") },
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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

            // Category picker
            Text("Assign Category", style = AppTypography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.categories) { cat ->
                    val isSelected = uiState.selectedCategoryId == cat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(cat.id) },
                        label = { Text(cat.name) },
                        shape = AppShapes.ChipCorner,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGreen,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Due day of month (1-31)
            OutlinedTextField(
                value = dueDayText,
                onValueChange = { input ->
                    dueDayText = input
                    val day = input.toIntOrNull()
                    if (day != null && day in 1..31) {
                        viewModel.setDueDay(day)
                    }
                },
                label = { Text("Day of Month Due (1 to 31)") },
                placeholder = { Text("1") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = AppShapes.InputFieldCorner,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("If a month has fewer days (e.g. Feb 28), it posts on the last day automatically.")
                }
            )

            uiState.errorMessage?.let { error ->
                Text(error, color = WarningRed, style = AppTypography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveRule(onSaved) },
                enabled = uiState.label.isNotBlank() && uiState.amountPaise > 0 && !uiState.isSaving,
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
                    Text(if (ruleId != null) "Update Bill" else "Save Recurring Bill", style = AppTypography.titleMedium)
                }
            }
        }
    }
}
