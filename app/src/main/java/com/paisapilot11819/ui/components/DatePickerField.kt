package com.paisapilot11819.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.paisapilot11819.ui.theme.AppShapes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String, // "yyyy-MM-dd"
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parsedDate = try {
        LocalDate.parse(selectedDate)
    } catch (_: Exception) {
        LocalDate.now()
    }

    val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val displayText = try {
        parsedDate.format(displayFormatter)
    } catch (_: Exception) {
        selectedDate
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
            },
            shape = AppShapes.InputFieldCorner,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val chosen = LocalDate.of(year, month + 1, dayOfMonth)
                            onDateSelected(chosen.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        },
                        parsedDate.year,
                        parsedDate.monthValue - 1,
                        parsedDate.dayOfMonth
                    ).show()
                },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

