package com.jeweljester.game.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.vm.ProgressViewModel

@Composable
fun SettingsScreen(nav: NavHostController) {
    val context = LocalContext.current
    val repo = rememberRepository()
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.factory(repo))
    val data by vm.gameData.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(title = "Settings", onBack = { nav.popBackStack() })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Switch(
                        checked = data.settings.soundEnabled,
                        onCheckedChange = { vm.setSound(it) }
                    )
                }
                JewelButton(text = "Privacy Policy") {
                    runCatching {
                        // TODO: замени на реальную ссылку политики конфиденциальности
                        val url = "https://example.com/jewel-jester/privacy"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
                JewelButton(text = "Rate App") {
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        )
                    }.onFailure {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                            )
                        )
                    }
                }
                JewelButton(text = "Reset Results") { showResetDialog = true }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all results?") },
            text = { Text("This will permanently remove all saved quiz scores and level progress on this device.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetAll()
                    showResetDialog = false
                    Toast.makeText(context, "Results reset", Toast.LENGTH_SHORT).show()
                }) { Text("Reset", color = Gold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}
