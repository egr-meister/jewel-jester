package com.jeweljester.game.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeweljester.game.R
import com.jeweljester.game.navigation.rememberRepository
import com.jeweljester.game.ui.LocaleManager
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.components.JewelButton
import com.jeweljester.game.ui.components.JewelTopBar
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.GoldDark
import com.jeweljester.game.ui.theme.JewelLight
import com.jeweljester.game.ui.theme.TextOnGold
import com.jeweljester.game.ui.theme.ThemeMode
import com.jeweljester.game.ui.theme.White
import com.jeweljester.game.ui.vm.ProgressViewModel

// Languages the app ships (see res/values-*). Each is labelled in its own language so a
// user who switched to an unfamiliar one can find the way back. "System" (null tag) first.
private data class LanguageOption(val tag: String?, val endonym: String)

private val LANGUAGES = listOf(
    LanguageOption("en", "English"),
    LanguageOption("ru", "Русский"),
    LanguageOption("fr", "Français"),
    LanguageOption("es", "Español"),
    LanguageOption("pt", "Português"),
    LanguageOption("it", "Italiano"),
    LanguageOption("de", "Deutsch"),
)

@Composable
fun SettingsScreen(nav: NavHostController) {
    val context = LocalContext.current
    val repo = rememberRepository()
    val vm: ProgressViewModel = viewModel(factory = ProgressViewModel.factory(repo))
    val data by vm.gameData.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val currentTag = LocaleManager.currentTag()
    val currentLanguageLabel = LANGUAGES.firstOrNull { it.tag == currentTag }?.endonym
        ?: stringResource(R.string.language_system)

    JewelBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            JewelTopBar(title = stringResource(R.string.label_settings), onBack = { nav.popBackStack() })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 40.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_sound), color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Switch(
                        checked = data.settings.soundEnabled,
                        onCheckedChange = { vm.setSound(it) }
                    )
                }

                // Theme
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.settings_theme), color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    val current = ThemeMode.from(data.settings.themeMode)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeChip(stringResource(R.string.theme_system), current == ThemeMode.SYSTEM, Modifier.weight(1f)) {
                            vm.setTheme(ThemeMode.SYSTEM.name)
                        }
                        ThemeChip(stringResource(R.string.theme_light), current == ThemeMode.LIGHT, Modifier.weight(1f)) {
                            vm.setTheme(ThemeMode.LIGHT.name)
                        }
                        ThemeChip(stringResource(R.string.theme_dark), current == ThemeMode.DARK, Modifier.weight(1f)) {
                            vm.setTheme(ThemeMode.DARK.name)
                        }
                    }
                }

                // Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_language), color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = currentLanguageLabel,
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, GoldDark, RoundedCornerShape(10.dp))
                            .clickable { showLanguageDialog = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                JewelButton(text = stringResource(R.string.settings_privacy_policy)) {
                    runCatching {
                        // TODO: замени на реальную ссылку политики конфиденциальности
                        val url = "https://example.com/jewel-jester/privacy"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
                JewelButton(text = stringResource(R.string.settings_rate_app)) {
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
                JewelButton(text = stringResource(R.string.settings_reset_results)) { showResetDialog = true }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.dialog_reset_title)) },
            text = { Text(stringResource(R.string.dialog_reset_message)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetAll()
                    showResetDialog = false
                    Toast.makeText(context, context.getString(R.string.toast_results_reset), Toast.LENGTH_SHORT).show()
                }) { Text(stringResource(R.string.action_reset), color = Gold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LanguageRow(stringResource(R.string.language_system), currentTag == null) {
                        LocaleManager.applyLocale(null)
                        showLanguageDialog = false
                    }
                    LANGUAGES.forEach { lang ->
                        LanguageRow(lang.endonym, currentTag == lang.tag) {
                            LocaleManager.applyLocale(lang.tag)
                            showLanguageDialog = false
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) TextOnGold else White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Gold else Color.Transparent)
            .border(2.dp, if (selected) Gold else GoldDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    )
}

@Composable
private fun LanguageRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Gold else JewelLight,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
        fontSize = 17.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}
