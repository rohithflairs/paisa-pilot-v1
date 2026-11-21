package com.paisapilot11819.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paisapilot11819.ui.theme.*

@Composable
fun BalanceCard(
    title: String,
    balancePaise: Long,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    val isOverBudget = balancePaise < 0
    val targetColor = when {
        isOverBudget -> WarningRed
        balancePaise == 0L -> WarningAmber
        else -> AccentGreen
    }

    val balanceColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(Anim.FADE_IN_MS),
        label = "balanceColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.CardCorner,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = AppTypography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = CurrencyUtils.formatPaise(balancePaise),
                style = AppTypography.balanceDisplay,
                color = balanceColor
            )

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${CurrencyUtils.formatPaiseCompact(kotlin.math.abs(balancePaise))} over budget",
                    style = AppTypography.labelLarge,
                    color = WarningRed
                )
            } else if (balancePaise == 0L) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Limit reached",
                    style = AppTypography.labelLarge,
                    color = WarningAmber
                )
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

