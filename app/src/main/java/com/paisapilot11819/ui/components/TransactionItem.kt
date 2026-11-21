package com.paisapilot11819.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.paisapilot11819.ui.theme.*

enum class TransactionType {
    EXPENSE, ADD_MONEY, REFUND
}

@Composable
fun TransactionItem(
    type: TransactionType,
    title: String,
    amountPaise: Long,
    date: String,
    subtitle: String? = null,
    isRecurring: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRefund: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expandedMenu by remember { mutableStateOf(false) }

    val icon: ImageVector = when (type) {
        TransactionType.EXPENSE -> Icons.Default.ArrowDownward
        TransactionType.ADD_MONEY -> Icons.Default.ArrowUpward
        TransactionType.REFUND -> Icons.Default.Replay
    }

    val iconBgColor: Color = when (type) {
        TransactionType.EXPENSE -> WarningRed.copy(alpha = 0.15f)
        TransactionType.ADD_MONEY -> SuccessGreen.copy(alpha = 0.15f)
        TransactionType.REFUND -> RefundBlue.copy(alpha = 0.15f)
    }

    val iconColor: Color = when (type) {
        TransactionType.EXPENSE -> WarningRed
        TransactionType.ADD_MONEY -> SuccessGreen
        TransactionType.REFUND -> RefundBlue
    }

    val amountPrefix = when (type) {
        TransactionType.EXPENSE -> "-"
        TransactionType.ADD_MONEY -> "+"
        TransactionType.REFUND -> "+"
    }

    val amountColor = when (type) {
        TransactionType.EXPENSE -> WarningRed
        TransactionType.ADD_MONEY -> SuccessGreen
        TransactionType.REFUND -> RefundBlue
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expandedMenu = true },
        shape = AppShapes.CardCorner,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type.name,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = AppTypography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isRecurring) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Recurring",
                            tint = AccentBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (subtitle != null) "$date • $subtitle" else date,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$amountPrefix${CurrencyUtils.formatPaiseCompact(amountPaise)}",
                style = AppTypography.titleLarge,
                color = amountColor
            )

            // Action menu
            DropdownMenu(
                expanded = expandedMenu,
                onDismissRequest = { expandedMenu = false }
            ) {
                if (onEdit != null) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            expandedMenu = false
                            onEdit()
                        }
                    )
                }
                if (onRefund != null && type == TransactionType.EXPENSE) {
                    DropdownMenuItem(
                        text = { Text("Record Refund") },
                        leadingIcon = { Icon(Icons.Default.Replay, contentDescription = null) },
                        onClick = {
                            expandedMenu = false
                            onRefund()
                        }
                    )
                }
                if (onDelete != null) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = WarningRed) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = WarningRed) },
                        onClick = {
                            expandedMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
