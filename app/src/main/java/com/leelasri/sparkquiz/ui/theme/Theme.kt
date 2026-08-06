package com.leelasri.sparkquiz.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SparkDarkScheme = darkColorScheme(
    primary = SparkAmberDark,
    onPrimary = OnSparkAmberDark,
    secondary = VoltDark,
    onSecondary = OnVoltDark,
    tertiary = AndroidGreenDark,
    onTertiary = OnAndroidGreenDark,
    error = IncorrectRedDark,
    onError = OnIncorrectRedDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark
)

private val SparkLightScheme = lightColorScheme(
    primary = SparkAmberLight,
    onPrimary = OnSparkAmberLight,
    secondary = VoltLight,
    onSecondary = OnVoltLight,
    tertiary = AndroidGreenLight,
    onTertiary = OnAndroidGreenLight,
    error = IncorrectRedLight,
    onError = OnIncorrectRedLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight
)

/**
 * SparkQuiz's fixed brand identity. Deliberately NOT using dynamic (Material You) color —
 * a quiz app should look like itself on every device, not tint itself to the user's wallpaper.
 * It still respects the system dark/light switch, just between two hand-picked brand schemes
 * instead of one generic template scheme.
 */
@Composable
fun SparkQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SparkDarkScheme else SparkLightScheme

    // Explicitly pin status/nav bar icon color to the resolved theme instead of trusting
    // OEM defaults, which is exactly why this looked inconsistent device to device before —
    // dark icons on the light scheme, light icons on the dark scheme, always.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}