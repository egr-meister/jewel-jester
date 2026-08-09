package com.jeweljester.game.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.jeweljester.game.data.QuizData
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.ui.components.JewelBackground

@Composable
fun QuizResultsScreen(nav: NavHostController, categoryId: String, score: Int) {
    val category = QuizData.byId(categoryId)
    val total = category.questions.size

    JewelBackground {
        ResultPanel(
            title = category.title,
            subtitle = "Score: $score/$total",
            onContinue = {
                nav.navigate(Routes.QUIZ_MENU) { popUpTo(Routes.QUIZ_MENU) { inclusive = true } }
            },
            onRestart = {
                nav.navigate(Routes.quiz(categoryId)) { popUpTo(Routes.QUIZ_MENU) }
            },
            onMenu = { nav.navigate(Routes.MENU) { popUpTo(Routes.MENU) } }
        )
    }
}
