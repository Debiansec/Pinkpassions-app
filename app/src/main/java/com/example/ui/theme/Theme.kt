package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.data.AppThemeMode

/**
 * Pink Passions Custom Material 3 Color System
 * Signature deep pink (#E31999) paired with dark charcoal neutrals for a luxurious, refined aesthetic.
 */
object PinkPassionsColors {
    // Signature Deep Pinks & Accents
    val SignaturePink = Color(0xFFE31999)
    val DeepPink = Color(0xFFE31999)
    val MagentaPink = Color(0xFFC71485)
    val HotPink = Color(0xFFFF2A9D)
    val VelvetPink = Color(0xFF9E0B68)
    val RosePink = Color(0xFFFF6BB8)
    val WinePink = Color(0xFF5E053B)
    val PinkGlow = Color(0x33E31999)
    val SubtlePink = Color(0x1AE31999)
    val GoldAccent = Color(0xFFFFD700)

    // Dark Charcoal Neutrals & Surfaces
    val CharcoalVoid = Color(0xFF09090D)
    val CharcoalBackground = Color(0xFF0E0E14)
    val CharcoalSurface = Color(0xFF15151F)
    val CharcoalCard = Color(0xFF1D1D2A)
    val CharcoalElevated = Color(0xFF252536)
    val CharcoalHighlight = Color(0xFF2E2E42)
    val CharcoalBorder = Color(0xFF2C2C3E)
    val CharcoalBorderPink = Color(0x4DE31999)

    // Light Neutral Texts
    val TextPrimary = Color(0xFFF6F6FA)
    val TextSecondary = Color(0xFFA6A6BC)
    val TextTertiary = Color(0xFF6E6E84)
}

/**
 * Material 3 Dark ColorScheme with Deep Pink (#E31999) and Dark Charcoal Neutrals
 */
val PinkPassionsDarkColorScheme: ColorScheme = darkColorScheme(
    // Primary - Signature Deep Pink
    primary = PinkPassionsColors.SignaturePink,
    onPrimary = Color.White,
    primaryContainer = PinkPassionsColors.WinePink,
    onPrimaryContainer = Color(0xFFFFD8E7),
    inversePrimary = PinkPassionsColors.RosePink,

    // Secondary - Elegant Rose / Accent
    secondary = PinkPassionsColors.RosePink,
    onSecondary = PinkPassionsColors.CharcoalVoid,
    secondaryContainer = Color(0xFF3B1429),
    onSecondaryContainer = Color(0xFFFFD9E4),

    // Tertiary - Luxury Gold / Highlights
    tertiary = PinkPassionsColors.GoldAccent,
    onTertiary = PinkPassionsColors.CharcoalVoid,
    tertiaryContainer = Color(0xFF3D2E00),
    onTertiaryContainer = Color(0xFFFFDF9E),

    // Background & Surfaces - Dark Charcoal
    background = PinkPassionsColors.CharcoalBackground,
    onBackground = PinkPassionsColors.TextPrimary,

    surface = PinkPassionsColors.CharcoalSurface,
    onSurface = PinkPassionsColors.TextPrimary,
    surfaceVariant = PinkPassionsColors.CharcoalCard,
    onSurfaceVariant = PinkPassionsColors.TextSecondary,
    surfaceTint = PinkPassionsColors.SignaturePink,
    surfaceContainer = PinkPassionsColors.CharcoalCard,
    surfaceContainerHigh = PinkPassionsColors.CharcoalElevated,
    surfaceContainerHighest = PinkPassionsColors.CharcoalHighlight,
    surfaceContainerLow = PinkPassionsColors.CharcoalSurface,
    surfaceContainerLowest = PinkPassionsColors.CharcoalVoid,
    inverseSurface = Color(0xFFE6E6EE),
    inverseOnSurface = PinkPassionsColors.CharcoalBackground,

    // Outlines & Borders
    outline = PinkPassionsColors.CharcoalBorder,
    outlineVariant = PinkPassionsColors.CharcoalBorderPink,
    scrim = Color.Black,

    // Errors
    error = Color(0xFFFF4D4F),
    onError = Color.White,
    errorContainer = Color(0xFF5C0008),
    onErrorContainer = Color(0xFFFFDAD6)
)

/**
 * Material 3 Light ColorScheme Variant with Deep Pink (#E31999) and Soft Charcoal/Warm Light Neutrals
 */
val PinkPassionsLightColorScheme: ColorScheme = lightColorScheme(
    primary = PinkPassionsColors.DeepPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E7),
    onPrimaryContainer = PinkPassionsColors.WinePink,
    inversePrimary = PinkPassionsColors.RosePink,

    secondary = PinkPassionsColors.MagentaPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDE7F2),
    onSecondaryContainer = Color(0xFF3F0028),

    tertiary = Color(0xFF9E7200),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEFA8),
    onTertiaryContainer = Color(0xFF281C00),

    background = Color(0xFFFAF9FC),
    onBackground = Color(0xFF16161E),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF16161E),
    surfaceVariant = Color(0xFFF0EFF5),
    onSurfaceVariant = Color(0xFF545464),
    surfaceTint = PinkPassionsColors.DeepPink,
    inverseSurface = PinkPassionsColors.CharcoalElevated,
    inverseOnSurface = Color(0xFFF5F5FA),

    outline = Color(0xFFD6D5E0),
    outlineVariant = Color(0xFFFFC0DA),
    scrim = Color.Black,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

/**
 * Material 3 AMOLED Pure Black ColorScheme
 */
val PinkPassionsAmoledColorScheme: ColorScheme = darkColorScheme(
    primary = PinkPassionsColors.SignaturePink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF380020),
    onPrimaryContainer = Color(0xFFFFD8E7),
    inversePrimary = PinkPassionsColors.DeepPink,

    secondary = PinkPassionsColors.RosePink,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF240615),
    onSecondaryContainer = Color(0xFFFFD9E4),

    tertiary = PinkPassionsColors.GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2E2200),
    onTertiaryContainer = Color(0xFFFFDF9E),

    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF08080C),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF12121A),
    onSurfaceVariant = Color(0xFFA6A6BC),
    surfaceTint = PinkPassionsColors.SignaturePink,
    inverseSurface = Color(0xFFE6E6EE),
    inverseOnSurface = Color(0xFF000000),

    outline = Color(0xFF22222E),
    outlineVariant = Color(0x66E31999),
    scrim = Color.Black,

    error = Color(0xFFFF4D4F),
    onError = Color.White,
    errorContainer = Color(0xFF5C0008),
    onErrorContainer = Color(0xFFFFDAD6)
)

/**
 * Brand Theme Entry Composable for Pink Passions
 */
@Composable
fun PinkPassionsTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK -> PinkPassionsDarkColorScheme
        AppThemeMode.AMOLED -> PinkPassionsAmoledColorScheme
        AppThemeMode.LIGHT -> PinkPassionsLightColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) PinkPassionsDarkColorScheme else PinkPassionsLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
