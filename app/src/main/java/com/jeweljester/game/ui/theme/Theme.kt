package com.jeweljester.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Theme choice stored in settings. SYSTEM follows the device. */
enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun from(value: String?): ThemeMode =
            runCatching { valueOf(value ?: SYSTEM.name) }.getOrDefault(SYSTEM)
    }
}

private val JewelDarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = TextOnGold,
    secondary = JewelLight,
    background = JewelDeep,
    onBackground = White,
    surface = JewelPurple,
    onSurface = White,
)

private val JewelLightColors = lightColorScheme(
    primary = GoldDark,
    onPrimary = White,
    secondary = JewelLight,
    background = JewelDeepLight,
    onBackground = TextOnLight,
    surface = JewelPurpleLight,
    onSurface = TextOnLight,
)

@Composable
fun JewelJesterTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) JewelDarkColors else JewelLightColors,
        typography = JewelTypography,
        content = content,
    )
}
