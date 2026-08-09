package com.jeweljester.game.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelPanel
import com.jeweljester.game.ui.theme.White

@Composable
fun GameRulesScreen(nav: NavHostController) {
    JewelBackground {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            JewelPanel {
                PanelTitle("Game Rules")
                Text(
                    text = "Match identical jewels to complete the game. " +
                        "Flip two cards: if they match, they stay open. " +
                        "Open all pairs before the time runs out to win.",
                    color = White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                JewelButton("Continue") { nav.popBackStack() }
            }
        }
    }
}
