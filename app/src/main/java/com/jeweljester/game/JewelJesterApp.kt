package com.jeweljester.game

import android.app.Application
import com.jeweljester.game.audio.SoundManager
import com.jeweljester.game.data.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Простой ручной service-locator без DI-фреймворка: единственный репозиторий
 * живёт на уровне приложения и достаётся из ViewModel-фабрик.
 */
class JewelJesterApp : Application() {

    val repository: GameRepository by lazy { GameRepository(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        SoundManager.init(this)
        // Синхронизируем настройку звука из сохранённого состояния.
        appScope.launch {
            repository.gameData
                .map { it.settings.soundEnabled }
                .collect { enabled -> SoundManager.setEnabled(enabled) }
        }
    }
}
