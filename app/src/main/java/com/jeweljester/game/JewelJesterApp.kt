package com.jeweljester.game

import android.app.Application
import com.jeweljester.game.data.GameRepository

/**
 * Простой ручной service-locator без DI-фреймворка: единственный репозиторий
 * живёт на уровне приложения и достаётся из ViewModel-фабрик.
 */
class JewelJesterApp : Application() {

    val repository: GameRepository by lazy { GameRepository(this) }
}
