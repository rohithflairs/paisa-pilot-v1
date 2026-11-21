package com.paisapilot11819.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.paisapilot11819.ui.theme.AccentGreen
import com.paisapilot11819.ui.theme.AppShapes
import com.paisapilot11819.ui.theme.WarningRed

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        shape = AppShapes.DialogCorner,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) WarningRed else AccentGreen
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
