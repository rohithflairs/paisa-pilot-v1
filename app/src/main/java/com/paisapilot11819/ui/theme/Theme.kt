package com.paisapilot11819.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.style.TextAlign

// ═══════════════════════════════════════════════════════════════════
// 🎨 PAISA PILOT — SINGLE-FILE THEME CONFIGURATION
// ═══════════════════════════════════════════════════════════════════
// Change ANY value below and rebuild to retheme the entire app.
// No other file in the project contains hardcoded colors or animation values.
// ═══════════════════════════════════════════════════════════════════

// ───── BACKGROUND & SURFACE COLORS ─────
val BackgroundDark       = Color(0xFF0D1B2A)   // Deep Navy — main background
val SurfaceDark          = Color(0xFF1B2838)   // Slightly lighter — cards, sheets
val SurfaceVariantDark   = Color(0xFF243447)   // Elevated surface — dialogs, FABs
val BackgroundLight      = Color(0xFFF5F7FA)   // Light mode background
val SurfaceLight         = Color(0xFFFFFFFF)   // Light mode cards
val SurfaceVariantLight  = Color(0xFFEEF1F6)   // Light mode elevated

// ───── ACCENT & BRAND COLORS ─────
val AccentGreen          = Color(0xFF00C896)   // Emerald/Mint — primary accent
val AccentGreenDark      = Color(0xFF00A67E)   // Pressed / active state
val AccentGreenLight     = Color(0xFF33D4AB)   // Light variant for indicators
val AccentBlue           = Color(0xFF4FC3F7)   // Secondary — info, links

// ───── SEMANTIC COLORS ─────
val WarningRed           = Color(0xFFFF4D4D)   // Overspend / negative / error
val WarningRedDark       = Color(0xFFD32F2F)   // Severe overspend
val WarningAmber         = Color(0xFFFFB020)   // Approaching limit / caution
val SuccessGreen         = Color(0xFF4CAF50)   // Income / add money / success
val RefundBlue           = Color(0xFF42A5F5)   // Refund indicator

// ───── TEXT COLORS ─────
val TextPrimaryDark      = Color(0xFFFFFFFF)   // Main text on dark
val TextSecondaryDark    = Color(0xFFB0BEC5)   // Subtle labels on dark
val TextTertiaryDark     = Color(0xFF78909C)   // Muted / disabled on dark
val TextPrimaryLight     = Color(0xFF1A1A2E)   // Main text on light
val TextSecondaryLight   = Color(0xFF546E7A)   // Subtle labels on light
val TextTertiaryLight    = Color(0xFF90A4AE)   // Muted on light

// ───── DIVIDERS & OUTLINES ─────
val DividerDark          = Color(0xFF2A3A4A)   // Separator lines on dark
val DividerLight         = Color(0xFFE0E0E0)   // Separator lines on light
val OutlineDark          = Color(0xFF37474F)   // Input field borders on dark
val OutlineLight         = Color(0xFFBDBDBD)   // Input field borders on light

// ───── CHART COLORS (category spending bars) ─────
val ChartColors = listOf(
    Color(0xFF00C896),  // Emerald
    Color(0xFF4FC3F7),  // Sky Blue
    Color(0xFFFFB020),  // Amber
    Color(0xFFE040FB),  // Purple
    Color(0xFFFF7043),  // Deep Orange
    Color(0xFF66BB6A),  // Green
    Color(0xFFAB47BC),  // Violet
    Color(0xFF29B6F6),  // Light Blue
    Color(0xFFFFCA28),  // Yellow
    Color(0xFFEF5350),  // Red
)

// ═══════════════════════════════════════════════════════════════════
// 🎬 ANIMATION CONSTANTS
// ═══════════════════════════════════════════════════════════════════
// Minimal, purposeful animations per spec requirement.

object Anim {
    const val FADE_IN_MS         = 250
    const val FADE_OUT_MS        = 200
    const val SLIDE_UP_MS        = 300
    const val SLIDE_DOWN_MS      = 250
    const val BUTTON_PRESS_MS    = 100
    const val SHEET_EXPAND_MS    = 350
    const val CARD_EXPAND_MS     = 300
    const val PROGRESS_BAR_MS   = 400
    val EASING = FastOutSlowInEasing

    fun <T> defaultTween() = tween<T>(
        durationMillis = FADE_IN_MS,
        easing = EASING
    )
}

// ═══════════════════════════════════════════════════════════════════
// 📐 SHAPES
// ═══════════════════════════════════════════════════════════════════

object AppShapes {
    val CardCorner        = RoundedCornerShape(16.dp)
    val ButtonCorner      = RoundedCornerShape(12.dp)
    val BottomSheetCorner = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val ChipCorner        = RoundedCornerShape(8.dp)
    val InputFieldCorner  = RoundedCornerShape(12.dp)
    val DialogCorner      = RoundedCornerShape(20.dp)
    val SmallCorner       = RoundedCornerShape(8.dp)
}

// ═══════════════════════════════════════════════════════════════════
// 🔤 TYPOGRAPHY
// ═══════════════════════════════════════════════════════════════════

object AppTypography {
    val displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    val displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )
    val headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )
    val headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )
    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
    val titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
    // Special: large balance display
    val balanceDisplay = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    )
    // Special: amount input
    val amountInput = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        textAlign = TextAlign.Center
    )
}

// ═══════════════════════════════════════════════════════════════════
// 📏 SPACING & DIMENSIONS
// ═══════════════════════════════════════════════════════════════════

object AppDimens {
    val screenPadding = 16.dp
    val cardPadding = 16.dp
    val itemSpacing = 12.dp
    val sectionSpacing = 24.dp
    val iconSize = 24.dp
    val iconSizeLarge = 32.dp
    val buttonHeight = 56.dp
    val inputHeight = 56.dp
    val progressBarHeight = 8.dp
    val dividerThickness = 1.dp
    val bottomNavHeight = 80.dp
}

// ═══════════════════════════════════════════════════════════════════
// 🎨 MATERIAL 3 COLOR SCHEMES
// ═══════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = Color.White,
    primaryContainer = AccentGreenDark,
    onPrimaryContainer = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = WarningRed,
    onError = Color.White,
    outline = OutlineDark,
    outlineVariant = DividerDark,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentGreen,
    onPrimary = Color.White,
    primaryContainer = AccentGreenLight,
    onPrimaryContainer = Color.Black,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = WarningRed,
    onError = Color.White,
    outline = OutlineLight,
    outlineVariant = DividerLight,
)

// ═══════════════════════════════════════════════════════════════════
// 🏗️ THEME COMPOSABLE
// ═══════════════════════════════════════════════════════════════════

@Composable
fun PaisaPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
