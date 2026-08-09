package com.jeweljester.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.data.Assets
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.GoldDark
import com.jeweljester.game.ui.theme.JewelLight
import com.jeweljester.game.ui.theme.JewelPurple
import com.jeweljester.game.ui.theme.TextOnGold
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.vm.Card
import com.jeweljester.game.ui.vm.Outcome
import com.jeweljester.game.ui.vm.PairsViewModel

@Composable
fun PairsGameScreen(nav: NavHostController, level: Int) {
    val repo = rememberRepository()
    val vm: PairsViewModel = viewModel(factory = PairsViewModel.factory(repo, level))
    val state by vm.ui.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(state.outcome) {
        if (state.outcome != Outcome.PLAYING) {
            nav.navigate(
                Routes.pairsResults(level, state.matchedPairs, state.outcome == Outcome.WON)
            ) { popUpTo(Routes.LEVELS) }
        }
    }

    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(
                title = "Level $level",
                onBack = { nav.popBackStack() },
                onPause = { vm.pause() }
            )

            Text(
                text = "⏱ ${state.timeLeft}s   •   ${state.matchedPairs}/${state.totalPairs}",
                color = Gold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(state.columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(state.cards, key = { _, c -> c.id }) { index, card ->
                    CardView(card = card, onClick = { vm.onCardClick(index) })
                }
            }
        }

        if (state.paused) {
            PausePanel(
                onContinue = { vm.resume() },
                onRestart = { vm.restart() },
                onMenu = { nav.navigate(Routes.MENU) { popUpTo(Routes.MENU) } }
            )
        }
    }
}

@Composable
private fun CardView(card: Card, onClick: () -> Unit) {
    val faceOpen = card.faceUp || card.matched
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    if (faceOpen) listOf(JewelLight, JewelPurple) else listOf(Gold, GoldDark)
                )
            )
            .border(3.dp, if (card.matched) White else GoldDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (faceOpen) {
            Image(
                painter = painterResource(id = card.jewelRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        } else {
            Text("★", color = TextOnGold, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
        }
    }
}
