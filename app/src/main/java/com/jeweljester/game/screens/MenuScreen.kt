package com.jeweljester.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jeweljester.game.data.Assets
import com.jeweljester.game.navigation.Routes
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton

@Composable
fun MenuScreen(nav: NavHostController) {
    JewelBackground {
        Box(modifier = Modifier.fillMaxSize()) {

            // Двое джокеров обрамляют экран снизу по углам.
            Image(
                painter = painterResource(id = Assets.jesterA),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .height(230.dp)
                    .padding(start = 4.dp)
            )
            Image(
                painter = painterResource(id = Assets.jesterB),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(230.dp)
                    .padding(end = 4.dp)
            )

            // Кнопки меню по центру.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
            ) {
                JewelButton(text = "Quiz") { nav.navigate(Routes.QUIZ_MENU) }
                JewelButton(text = "Matching Pairs") { nav.navigate(Routes.LEVELS) }
                JewelButton(text = "Results") { nav.navigate(Routes.RESULTS) }
                JewelButton(text = "Settings") { nav.navigate(Routes.SETTINGS) }
            }
        }
    }
}
