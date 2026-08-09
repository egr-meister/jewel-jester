package com.jeweljester.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.data.Assets
import com.jeweljester.game.data.GameLogic
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.JewelDeep
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.vm.ProgressViewModel

@Composable
fun LevelsScreen(nav: NavHostController) {
    val repo = rememberRepository()
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.factory(repo))
    val data by vm.gameData.collectAsStateWithLifecycle()

    JewelBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JewelTopBar(title = "Levels", onBack = { nav.popBackStack() })

            // Цифры уровней — сразу под заголовком (выше по экрану).
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                (0 until 3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..3).forEach { col ->
                            val level = row * 3 + col
                            LevelBubble(
                                level = level,
                                unlocked = GameLogic.isLevelUnlocked(data.pairsBest, level),
                                onClick = { nav.navigate(Routes.pairs(level)) }
                            )
                        }
                    }
                }
            }

            // Большой джокер-мальчик под цифрами (занимает свободное место).
            Image(
                painter = painterResource(id = Assets.jesterA),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.72f)
                    .padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)) {
                JewelButton("Game Rules") { nav.navigate(Routes.RULES) }
            }
        }
    }
}

@Composable
private fun LevelBubble(level: Int, unlocked: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(84.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = Assets.plateRound),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (unlocked) 1f else 0.45f)
                .clip(CircleShape)
                .clickable(enabled = unlocked, onClick = onClick)
        )
        if (unlocked) {
            Text(
                text = level.toString(),
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(JewelDeep)
                    .border(2.dp, White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", fontSize = 14.sp)
            }
        }
    }
}
