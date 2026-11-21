package com.paisapilot11819.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paisapilot11819.ui.theme.*

@Composable
fun BudgetProgressBar(
    categoryName: String,
    spentPaise: Long,
    budgetPaise: Long,
    barColor: Color = AccentGreen,
    modifier: Modifier = Modifier
) {
    val progress = if (budgetPaise > 0) {
        (spentPaise.toFloat() / budgetPaise.toFloat()).coerceAtLeast(0f)
    } else 0f

    val isOverBudget = spentPaise > budgetPaise
    val isLimitReached = spentPaise == budgetPaise && budgetPaise > 0

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(1f),
        animationSpec = tween(Anim.PROGRESS_BAR_MS),
        label = "progressBar"
    )

    val actualColor = when {
        isOverBudget -> WarningRed
        progress >= 0.85f -> WarningAmber
        else -> barColor
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = AppTypography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${CurrencyUtils.formatPaiseCompact(spentPaise)} / ${CurrencyUtils.formatPaiseCompact(budgetPaise)}",
                style = AppTypography.bodyMedium,
                color = if (isOverBudget) WarningRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.progressBarHeight)
                .clip(AppShapes.SmallCorner)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(AppShapes.SmallCorner)
                    .background(actualColor)
            )
        }

        if (isOverBudget) {
            val overAmount = spentPaise - budgetPaise
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$categoryName · ${CurrencyUtils.formatPaiseCompact(overAmount)} over budget",
                style = AppTypography.labelSmall,
                color = WarningRed
            )
        } else if (isLimitReached) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$categoryName · Limit reached",
                style = AppTypography.labelSmall,
                color = WarningAmber
            )
        }
    }
}

