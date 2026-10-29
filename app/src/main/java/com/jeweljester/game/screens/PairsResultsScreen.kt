package com.jeweljester.game.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.jeweljester.game.R
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
            title = if (won) stringResource(R.string.state_well_done) else stringResource(R.string.state_out_of_time),
            subtitle = stringResource(R.string.fmt_pairs, matched, config.pairs),
            continueLabel = if (won && hasNext) stringResource(R.string.action_next_level) else stringResource(R.string.action_continue),
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
