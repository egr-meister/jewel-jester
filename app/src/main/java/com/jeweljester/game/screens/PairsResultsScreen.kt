package com.jeweljester.game.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.jeweljester.game.data.PairsData
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.ui.components.JewelBackground

@Composable
fun PairsResultsScreen(
    nav: NavHostController,
    level: Int,
    matched: Int,
    won: Boolean
) {
    val config = PairsData.byLevel(level)
    val hasNext = level < PairsData.TOTAL_LEVELS

    JewelBackground {
        ResultPanel(
            title = if (won) "Well Done" else "Out of Time",
            subtitle = "Pairs: $matched/${config.pairs}",
            continueLabel = if (won && hasNext) "Next Level" else "Continue",
            onContinue = {
                if (won && hasNext) {
                    nav.navigate(Routes.pairs(level + 1)) { popUpTo(Routes.LEVELS) }
                } else {
                    nav.navigate(Routes.LEVELS) { popUpTo(Routes.LEVELS) { inclusive = true } }
                }
            },
            onRestart = { nav.navigate(Routes.pairs(level)) { popUpTo(Routes.LEVELS) } },
            onMenu = { nav.navigate(Routes.MENU) { popUpTo(Routes.MENU) } }
        )
    }
}
