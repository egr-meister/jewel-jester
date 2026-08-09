package com.jeweljester.game.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelPanel
import com.jeweljester.game.ui.components.ModalScrim
import com.jeweljester.game.ui.theme.White

@Composable
fun PanelTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PausePanel(
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    ModalScrim {
        JewelPanel {
            PanelTitle("Pause")
            Spacer(Modifier.height(4.dp))
            JewelButton("Continue") { onContinue() }
            JewelButton("Restart") { onRestart() }
            JewelButton("Menu") { onMenu() }
        }
    }
}

@Composable
fun ResultPanel(
    title: String,
    subtitle: String? = null,
    continueLabel: String = "Continue",
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    ModalScrim {
        JewelPanel {
            PanelTitle("Results")
            Text(
                text = title.uppercase(),
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(4.dp))
            JewelButton(continueLabel) { onContinue() }
            JewelButton("Restart") { onRestart() }
            JewelButton("Menu") { onMenu() }
        }
    }
}
