package com.jeweljester.game.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jeweljester.game.R
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
            PanelTitle(stringResource(R.string.title_pause))
            Spacer(Modifier.height(4.dp))
            JewelButton(stringResource(R.string.action_continue)) { onContinue() }
            JewelButton(stringResource(R.string.action_restart)) { onRestart() }
            JewelButton(stringResource(R.string.action_menu)) { onMenu() }
        }
    }
}

@Composable
fun ResultPanel(
    title: String,
    subtitle: String? = null,
    continueLabel: String? = null,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    val continueText = continueLabel ?: stringResource(R.string.action_continue)
    ModalScrim {
        JewelPanel {
            PanelTitle(stringResource(R.string.title_results))
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
            JewelButton(continueText) { onContinue() }
            JewelButton(stringResource(R.string.action_restart)) { onRestart() }
            JewelButton(stringResource(R.string.action_menu)) { onMenu() }
        }
    }
}
