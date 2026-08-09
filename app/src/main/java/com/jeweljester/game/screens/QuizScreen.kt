package com.jeweljester.game.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.CorrectGreen
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.GoldDark
import com.jeweljester.game.ui.theme.JewelLight
import com.jeweljester.game.ui.theme.JewelPurple
import com.jeweljester.game.ui.theme.TextOnGold
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.theme.WrongRed
import com.jeweljester.game.ui.vm.QuizViewModel

@Composable
fun QuizScreen(nav: NavHostController, categoryId: String) {
    val repo = rememberRepository()
    val vm: QuizViewModel = viewModel(factory = QuizViewModel.factory(repo, categoryId))
    val state by vm.ui.collectAsStateWithLifecycle()

    // Когда викторина завершена — уходим на экран результата.
    androidx.compose.runtime.LaunchedEffect(state.finished) {
        if (state.finished) {
            nav.navigate(Routes.quizResults(categoryId, state.score)) {
                popUpTo(Routes.QUIZ_MENU)
            }
        }
    }

    val progress by animateFloatAsState(
        targetValue = (state.index + 1f) / state.total,
        label = "progress"
    )

    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(
                title = state.title,
                onBack = { nav.popBackStack() },
                onPause = { vm.pause() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(JewelPurple)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(4.dp))
                        .background(Gold)
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(JewelLight, JewelPurple)))
                    .border(2.dp, Gold, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${state.index + 1}/${state.total}   ${state.question.text}",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.question.options.forEachIndexed { i, option ->
                    val answerState = when {
                        state.selected == null -> AnswerState.IDLE
                        i == state.question.correctIndex -> AnswerState.CORRECT
                        i == state.selected -> AnswerState.WRONG
                        else -> AnswerState.IDLE
                    }
                    AnswerOption(
                        text = option,
                        state = answerState,
                        enabled = state.selected == null && !state.paused,
                        onClick = { vm.onAnswer(i) }
                    )
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

private enum class AnswerState { IDLE, CORRECT, WRONG }

@Composable
private fun AnswerOption(
    text: String,
    state: AnswerState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = when (state) {
        AnswerState.IDLE -> listOf(Gold, GoldDark)
        AnswerState.CORRECT -> listOf(CorrectGreen, CorrectGreen)
        AnswerState.WRONG -> listOf(WrongRed, WrongRed)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(colors))
            .border(2.dp, GoldDark, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextOnGold,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
