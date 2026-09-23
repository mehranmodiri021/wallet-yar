package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.settings.AppSettingsState
import com.example.settings.ColorThemeMode

// Cyan Neon Scheme (Default)
private val DarkCyanColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004E5B),
    onPrimaryContainer = Color(0xFF80F5FF),
    secondary = GoldAccent,
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF5E4000),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = GreenSuccess,
    background = CryptoNavyDark,
    onBackground = TextPrimary,
    surface = CryptoSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CryptoSurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = CryptoBorderDark,
    error = RedAlert,
    onError = Color.White
)

private val LightCyanColorScheme = lightColorScheme(
    primary = Color(0xFF006874),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),
    secondary = Color(0xFF755B00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDF9E),
    onSecondaryContainer = Color(0xFF241A00),
    tertiary = Color(0xFF006D44),
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF191C1D),
    surface = Color.White,
    onSurface = Color(0xFF191C1D),
    surfaceVariant = Color(0xFFE1E7EE),
    onSurfaceVariant = Color(0xFF3F484A),
    outline = Color(0xFF6F797A),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

// Gold Amber Scheme
private val DarkGoldColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF5E4000),
    onPrimaryContainer = Color(0xFFFFDF9E),
    secondary = CyanPrimary,
    background = Color(0xFF15120B),
    surface = Color(0xFF1E1A12),
    surfaceVariant = Color(0xFF2E271E),
    outline = Color(0xFF473F30),
    error = RedAlert
)

// Emerald Green Matrix Scheme
private val DarkEmeraldColorScheme = darkColorScheme(
    primary = GreenSuccess,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF005333),
    onPrimaryContainer = Color(0xFF8CF8BD),
    secondary = CyanPrimary,
    background = Color(0xFF08140E),
    surface = Color(0xFF0E2117),
    surfaceVariant = Color(0xFF183325),
    outline = Color(0xFF274A37),
    error = RedAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = AppSettingsState.isDarkMode.value,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val themeMode = AppSettingsState.currentThemeMode.value

    val colorScheme: ColorScheme = if (darkTheme) {
        when (themeMode) {
            ColorThemeMode.CYAN_NEON -> DarkCyanColorScheme
            ColorThemeMode.GOLD_AMBER -> DarkGoldColorScheme
            ColorThemeMode.EMERALD_GREEN -> DarkEmeraldColorScheme
        }
    } else {
        LightCyanColorScheme
    }

    val currentDensity = LocalDensity.current
    val fontScale = AppSettingsState.fontScaleFactor.floatValue
    val adjustedDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale * fontScale
    )

    CompositionLocalProvider(LocalDensity provides adjustedDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
