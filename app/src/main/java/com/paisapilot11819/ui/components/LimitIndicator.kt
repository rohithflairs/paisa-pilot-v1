package com.paisapilot11819.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paisapilot11819.ui.theme.*

@Composable
fun LimitIndicator(
    label: String,
    spentPaise: Long,
    limitPaise: Long?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.CardCorner,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (limitPaise == null) "Spent: ${CurrencyUtils.formatPaiseCompact(spentPaise)}"
                    else "Spent: ${CurrencyUtils.formatPaiseCompact(spentPaise)} of ${CurrencyUtils.formatPaiseCompact(limitPaise)}",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (limitPaise == null) {
                Text(
                    text = "No limit set",
                    style = AppTypography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val remaining = limitPaise - spentPaise
                when {
                    remaining < 0 -> {
                        Text(
                            text = "${CurrencyUtils.formatPaiseCompact(-remaining)} over",
                            style = AppTypography.labelLarge,
                            color = WarningRed
                        )
                    }
                    remaining == 0L -> {
                        Text(
                            text = "Limit reached",
                            style = AppTypography.labelLarge,
                            color = WarningAmber
                        )
                    }
                    else -> {
                        Text(
                            text = "${CurrencyUtils.formatPaiseCompact(remaining)} left",
                            style = AppTypography.labelLarge,
                            color = AccentGreen
                        )
                    }
                }
            }
        }
    }
}

