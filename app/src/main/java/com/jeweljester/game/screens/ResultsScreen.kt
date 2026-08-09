package com.jeweljester.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.data.Assets
import com.jeweljester.game.data.GameLogic
import com.jeweljester.game.data.PairsData
import com.jeweljester.game.data.QuizData
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.JewelLight
import com.jeweljester.game.ui.theme.JewelPurple
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.vm.ProgressViewModel

@Composable
fun ResultsScreen(nav: NavHostController) {
    val repo = rememberRepository()
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.factory(repo))
    val data by vm.gameData.collectAsStateWithLifecycle()

    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(title = "Results", onBack = { nav.popBackStack() })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuizData.categories.forEach { category ->
                    ResultRow(
                        label = category.title,
                        value = "${data.quizBest[category.id] ?: 0}/${category.questions.size}"
                    )
                }
                ResultRow(
                    label = "Matching Pairs",
                    value = "${GameLogic.pairsCompletedCount(data.pairsBest)}/${PairsData.TOTAL_LEVELS}"
                )
            }
        }
        // Джокер приветствует результаты снизу слева.
        Image(
            painter = painterResource(id = Assets.jesterA),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(160.dp)
                .padding(start = 2.dp)
        )
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(JewelLight, JewelPurple)))
            .border(2.dp, Gold, RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(value, color = Gold, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
        }
    }
}
