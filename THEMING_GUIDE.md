# Paisa Pilot — Single-File Theming Guide 🎨

Welcome to the **Single-File Theming System** for Paisa Pilot!

Every color, font size, animation duration, and corner radius across the entire app is controlled by **one single file**:

📁 **`app/src/main/java/com/paisapilot11819/ui/theme/Theme.kt`**

No other file in the application contains hardcoded colors or animation values. Modifying this file will instantly retheme every screen, card, button, and indicator.

---

## 1. What You Can Customize in `Theme.kt`

### A. Background & Surface Colors
Change the app background and card elevations:
```kotlin
val BackgroundDark       = Color(0xFF0D1B2A)   // Main screen background
val SurfaceDark          = Color(0xFF1B2838)   // Card, dialog, and sheet surface
val SurfaceVariantDark   = Color(0xFF243447)   // Elevated cards / badges
```

### B. Brand & Semantic Colors
Control financial indicators, buttons, and alerts:
```kotlin
val AccentGreen          = Color(0xFF00C896)   // Primary buttons, remaining budget
val AccentGreenDark      = Color(0xFF00A67E)   // Pressed button state
val WarningRed           = Color(0xFFFF4D4D)   // Overspend alerts, expense button
val WarningAmber         = Color(0xFFFFB020)   // Approaching limit / caution
val SuccessGreen         = Color(0xFF4CAF50)   // Add Money button, income badge
val RefundBlue           = Color(0xFF42A5F5)   // Refund transactions & badges
```

### C. Text Colors
Ensure crisp readability:
```kotlin
val TextPrimaryDark      = Color(0xFFFFFFFF)   // Main headings, numbers
val TextSecondaryDark    = Color(0xFFB0BEC5)   // Subtitles, dates, notes
val TextTertiaryDark     = Color(0xFF78909C)   // Inactive labels
```

### D. Category Chart Colors
The color palette cycled through for category budget progress bars:
```kotlin
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
```

### E. Animation Speeds & Easing
Tune how fast screens, cards, and progress bars transition:
```kotlin
object Anim {
    const val FADE_IN_MS         = 250   // Balance number change fade
    const val FADE_OUT_MS        = 200
    const val SLIDE_UP_MS        = 300
    const val SLIDE_DOWN_MS      = 250
    const val BUTTON_PRESS_MS    = 100
    const val PROGRESS_BAR_MS   = 400   // Budget progress bar fill animation
    val EASING = FastOutSlowInEasing     // Compose animation easing curve
}
```

### F. Shapes & Corner Radii
Adjust how rounded cards, buttons, and dialogs appear:
```kotlin
object AppShapes {
    val CardCorner        = RoundedCornerShape(16.dp)
    val ButtonCorner      = RoundedCornerShape(12.dp)
    val BottomSheetCorner = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val ChipCorner        = RoundedCornerShape(8.dp)
    val InputFieldCorner  = RoundedCornerShape(12.dp)
    val DialogCorner      = RoundedCornerShape(20.dp)
}
```

---

## 2. Ready-to-Use Theme Presets

To apply any preset, copy and paste the block below into the top of `Theme.kt`:

### Preset 1: Default Sleek Deep Navy & Mint (Active)
```kotlin
val BackgroundDark       = Color(0xFF0D1B2A)
val SurfaceDark          = Color(0xFF1B2838)
val SurfaceVariantDark   = Color(0xFF243447)
val AccentGreen          = Color(0xFF00C896)
val AccentGreenDark      = Color(0xFF00A67E)
```

### Preset 2: Midnight Cyberpunk Purple
```kotlin
val BackgroundDark       = Color(0xFF120E24)
val SurfaceDark          = Color(0xFF1E1738)
val SurfaceVariantDark   = Color(0xFF2C224E)
val AccentGreen          = Color(0xFFB388FF)
val AccentGreenDark      = Color(0xFF7C4DFF)
```

### Preset 3: Minimalist Charcoal & Gold
```kotlin
val BackgroundDark       = Color(0xFF181818)
val SurfaceDark          = Color(0xFF242424)
val SurfaceVariantDark   = Color(0xFF333333)
val AccentGreen          = Color(0xFFFFD700)
val AccentGreenDark      = Color(0xFFC7A600)
```

### Preset 4: Nordic Forest Green
```kotlin
val BackgroundDark       = Color(0xFF0B1D13)
val SurfaceDark          = Color(0xFF142E20)
val SurfaceVariantDark   = Color(0xFF1E3F2D)
val AccentGreen          = Color(0xFF4EBA6F)
val AccentGreenDark      = Color(0xFF388E3C)
```

---

## 3. How to Apply Your Changes
1. Open `app/src/main/java/com/paisapilot11819/ui/theme/Theme.kt`.
2. Edit any hex color code (e.g., change `0xFF00C896` to your desired color).
3. Commit and push to GitHub:
   ```bash
   git add .
   git commit -m "Update theme colors"
   git push
   ```
4. GitHub Actions will rebuild the `.apk` with your new theme automatically!
