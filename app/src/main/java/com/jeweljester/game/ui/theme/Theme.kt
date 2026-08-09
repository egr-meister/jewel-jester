package com.jeweljester.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JewelColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = TextOnGold,
    secondary = JewelLight,
    background = JewelDeep,
    onBackground = White,
    surface = JewelPurple,
    onSurface = White,
)

@Composable
fun JewelJesterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JewelColorScheme,
        typography = JewelTypography,
        content = content
    )
}
