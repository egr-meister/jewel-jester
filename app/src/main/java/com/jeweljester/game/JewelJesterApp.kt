package com.jeweljester.game

import android.app.Application
import com.jeweljester.game.audio.SoundManager
import com.jeweljester.game.data.GameRepository
import com.jeweljester.game.integration.AccelerometerProbe
import com.jeweljester.game.integration.AdjustManager
import com.jeweljester.game.integration.DeviceSignals
import com.jeweljester.game.integration.IntegrationStorage
import com.jeweljester.game.integration.PushManager
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

        // White/Black integration. Order matters: storage first (everyone reads from it);
        // the accelerometer window is a whole second, so it must begin at launch, not where
        // the URL is built; the push token must be requested before the single click.
        IntegrationStorage.init(this)
        DeviceSignals.init(this)
        AccelerometerProbe.start(this)
        AdjustManager.init(this)
        PushManager.init(this)

        // Синхронизируем настройку звука из сохранённого состояния.
        appScope.launch {
            repository.gameData
                .map { it.settings.soundEnabled }
                .collect { enabled -> SoundManager.setEnabled(enabled) }
        }
    }
}
