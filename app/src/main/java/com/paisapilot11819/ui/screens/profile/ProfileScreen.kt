package com.paisapilot11819.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.ui.components.CurrencyUtils
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onMonthSelected: (Long) -> Unit,
    onBack: () -> Unit,
    onNavigateToSetup: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    // Export Document Launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && pendingExportJson != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(pendingExportJson!!.toByteArray())
                }
                Toast.makeText(context, "Backup saved successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                pendingExportJson = null
            }
        }
    }

    // Import Document Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().readText()
                }
                if (!json.isNullOrBlank()) {
                    viewModel.importData(json) { result ->
                        importStatusMessage = if (result.success) {
                            "Import Successful!\nRestored: ${result.counts.entries.joinToString(", ") { "${it.key}: ${it.value}" }}"
                        } else {
                            "Import Failed: ${result.message}"
                        }
                    }
                }
            } catch (e: Exception) {
                importStatusMessage = "Error reading backup file: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & History") },
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
            // Offline & Privacy Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.CardCorner,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AccentGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("100% Offline & Private", style = AppTypography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero internet permissions. All data, budgets, and logs are stored strictly on your phone.",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Backup & Restore Actions
            item {
                Text("Data Backup & Restore", style = AppTypography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.exportData { json ->
                                pendingExportJson = json
                                exportLauncher.launch("paisa_pilot_backup_${System.currentTimeMillis()}.json")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = AppShapes.ButtonCorner
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export JSON")
                    }

                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.weight(1f),
                        shape = AppShapes.ButtonCorner
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import JSON")
                    }
                }
            }

            // Setup New Month Button
            item {
                Button(
                    onClick = onNavigateToSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimens.buttonHeight),
                    shape = AppShapes.ButtonCorner,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Up New Month Plan", style = AppTypography.titleMedium)
                }
            }

            // Historical Months Section Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Monthly History", style = AppTypography.titleLarge)
            }

            if (uiState.monthSummaries.isEmpty()) {
                item {
                    Text(
                        "No completed months in history yet.",
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.monthSummaries, key = { it.monthPlan.id }) { summary ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMonthSelected(summary.monthPlan.id) },
                        shape = AppShapes.CardCorner,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = summary.monthPlan.yearMonth,
                                    style = AppTypography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Balance: ${CurrencyUtils.formatPaiseCompact(summary.remainingBalancePaise)}",
                                    style = AppTypography.titleMedium,
                                    color = if (summary.remainingBalancePaise < 0) WarningRed else AccentGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Opening: ${CurrencyUtils.formatPaiseCompact(summary.openingBalancePaise)}",
                                    style = AppTypography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Top-ups: +${CurrencyUtils.formatPaiseCompact(summary.addMoneyTotalPaise)}",
                                    style = AppTypography.bodySmall,
                                    color = SuccessGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (summary.hasZeroSpending) "No spending recorded"
                                    else "Net Spent: ${CurrencyUtils.formatPaiseCompact(summary.netSpendingPaise)}",
                                    style = AppTypography.bodySmall,
                                    color = if (summary.hasZeroSpending) MaterialTheme.colorScheme.onSurfaceVariant else WarningRed
                                )
                                if (summary.refundsTotalPaise > 0) {
                                    Text(
                                        text = "Refunds: +${CurrencyUtils.formatPaiseCompact(summary.refundsTotalPaise)}",
                                        style = AppTypography.bodySmall,
                                        color = RefundBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Import status alert
    importStatusMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { importStatusMessage = null },
            title = { Text("Backup Import") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { importStatusMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}

