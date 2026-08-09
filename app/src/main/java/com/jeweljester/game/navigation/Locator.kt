package com.jeweljester.game.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jeweljester.game.JewelJesterApp
import com.jeweljester.game.data.GameRepository

/** Достаёт единственный репозиторий из Application без DI-фреймворка. */
@Composable
fun rememberRepository(): GameRepository {
    val context = LocalContext.current
    return (context.applicationContext as JewelJesterApp).repository
}
