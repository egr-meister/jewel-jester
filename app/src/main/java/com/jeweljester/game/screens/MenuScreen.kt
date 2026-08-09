package com.jeweljester.game.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.MenuButton

@Composable
fun MenuScreen(nav: NavHostController) {
    val scroll = rememberScrollState()
    JewelBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(scroll)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Spacer(Modifier.height(28.dp))
            val w = Modifier.fillMaxWidth(0.70f)
            MenuButton(text = "Quiz", modifier = w) { nav.navigate(Routes.QUIZ_MENU) }
            MenuButton(text = "Pairs", modifier = w) { nav.navigate(Routes.LEVELS) }
            MenuButton(text = "Results", modifier = w) { nav.navigate(Routes.RESULTS) }
            MenuButton(text = "Settings", modifier = w) { nav.navigate(Routes.SETTINGS) }
            Spacer(Modifier.height(28.dp))
        }
    }
}
