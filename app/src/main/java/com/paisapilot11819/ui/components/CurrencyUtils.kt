package com.paisapilot11819.ui.components

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val formatter = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    private val wholeFormatter = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    /** Convert paise (Long) to display string like "₹1,00,000.50" or "-₹50.00" */
    fun formatPaise(paise: Long): String {
        val isNegative = paise < 0
        val absPaise = kotlin.math.abs(paise)
        val rupees = absPaise / 100.0
        val formatted = "₹${formatter.format(rupees)}"
        return if (isNegative) "-$formatted" else formatted
    }

    /** Convert paise (Long) to display string without decimals if whole: "₹1,00,000" */
    fun formatPaiseCompact(paise: Long): String {
        val isNegative = paise < 0
        val absPaise = kotlin.math.abs(paise)
        val formatted = if (absPaise % 100 == 0L) {
            "₹${wholeFormatter.format(absPaise / 100)}"
        } else {
            "₹${formatter.format(absPaise / 100.0)}"
        }
        return if (isNegative) "-$formatted" else formatted
    }

    /** Convert rupee input string to paise (Long) */
    fun rupeesToPaise(input: String): Long? {
        val cleaned = input.replace(",", "").replace("₹", "").trim()
        val dbl = cleaned.toDoubleOrNull() ?: return null
        if (dbl < 0) return null
        return Math.round(dbl * 100)
    }

    /** Convert paise to rupee double */
    fun paiseToRupees(paise: Long): Double = paise / 100.0
}
