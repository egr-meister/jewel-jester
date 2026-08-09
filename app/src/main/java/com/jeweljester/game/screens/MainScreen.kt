package com.jeweljester.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun MainScreen(nav: NavHostController) {
    JewelBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 40.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(id = Assets.logo),
                contentDescription = "Jewel Jester",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(170.dp)
            )
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(id = Assets.jester),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Spacer(Modifier.height(16.dp))
            JewelButton(text = "Start") { nav.navigate(Routes.MENU) }
            Spacer(Modifier.height(16.dp))
        }
    }
}
