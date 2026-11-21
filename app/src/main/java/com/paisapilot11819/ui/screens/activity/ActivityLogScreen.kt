package com.paisapilot11819.ui.screens.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paisapilot11819.PaisaPilotApp
import com.paisapilot11819.ui.components.*
import com.paisapilot11819.ui.theme.*
import com.paisapilot11819.viewmodel.ActivityItem
import com.paisapilot11819.viewmodel.ActivityLogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogScreen(
    monthPlanId: Long,
    onEditExpense: (Long) -> Unit,
    onAddRefund: (Long) -> Unit,
    onEditAddMoney: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PaisaPilotApp

    val viewModel: ActivityLogViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActivityLogViewModel(app, monthPlanId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    var itemToDelete by remember { mutableStateOf<ActivityItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Log") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Type Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.screenPadding, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedType == null,
                        onClick = { viewModel.filterByType(null) },
                        label = { Text("All Types") },
                        shape = AppShapes.ChipCorner
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedType == TransactionType.EXPENSE,
                        onClick = { viewModel.filterByType(TransactionType.EXPENSE) },
                        label = { Text("Expenses") },
                        shape = AppShapes.ChipCorner
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedType == TransactionType.ADD_MONEY,
                        onClick = { viewModel.filterByType(TransactionType.ADD_MONEY) },
                        label = { Text("Top-Ups") },
                        shape = AppShapes.ChipCorner
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedType == TransactionType.REFUND,
                        onClick = { viewModel.filterByType(TransactionType.REFUND) },
                        label = { Text("Refunds") },
                        shape = AppShapes.ChipCorner
                    )
                }
            }

            // Category Filter Chips Row
            if (uiState.categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimens.screenPadding, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategoryId == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("All Categories") },
                            shape = AppShapes.ChipCorner
                        )
                    }
                    items(uiState.categories) { cat ->
                        FilterChip(
                            selected = uiState.selectedCategoryId == cat.id,
                            onClick = { viewModel.filterByCategory(cat.id) },
                            label = { Text(cat.name) },
                            shape = AppShapes.ChipCorner
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction Items List
            if (uiState.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.ReceiptLong,
                        title = "No Transactions Found",
                        subtitle = "Expenses, top-ups, and refunds recorded for this month will appear here."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppDimens.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.itemSpacing),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.filteredItems, key = { "${it.type}_${it.id}" }) { item ->
                        TransactionItem(
                            type = item.type,
                            title = item.title,
                            amountPaise = item.amountPaise,
                            date = item.date,
                            subtitle = item.subtitle,
                            isRecurring = item.isRecurring,
                            onEdit = when (item.type) {
                                TransactionType.EXPENSE -> { { onEditExpense(item.id) } }
                                TransactionType.ADD_MONEY -> { { onEditAddMoney(item.id) } }
                                TransactionType.REFUND -> null
                            },
                            onDelete = { itemToDelete = item },
                            onRefund = if (item.type == TransactionType.EXPENSE) {
                                { onAddRefund(item.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        ConfirmDialog(
            title = "Delete ${item.type.name.replace("_", " ")}?",
            message = "Are you sure you want to delete this entry of ${CurrencyUtils.formatPaiseCompact(item.amountPaise)}? This will recalculate the month balance.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                when (item.type) {
                    TransactionType.EXPENSE -> item.rawExpense?.let { viewModel.deleteExpense(it) }
                    TransactionType.ADD_MONEY -> item.rawAddMoney?.let { viewModel.deleteAddMoney(it) }
                    TransactionType.REFUND -> item.rawRefund?.let { viewModel.deleteRefund(it) }
                }
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

