package com.jeweljester.game.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jeweljester.game.data.QuizData
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelTopBar

@Composable
fun QuizMenuScreen(nav: NavHostController) {
    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(title = "Quiz", onBack = { nav.popBackStack() })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                QuizData.categories.forEach { category ->
                    JewelButton(text = category.title) {
                        nav.navigate(Routes.quiz(category.id))
                    }
                }
            }
        }
    }
}
