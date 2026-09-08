package com.jeweljester.game

import android.app.Application
import com.jeweljester.game.audio.SoundManager
import com.jeweljester.game.data.GameRepository
import com.jeweljester.game.integration.AppsFlyerManager
import com.jeweljester.game.integration.IntegrationStorage
import com.jeweljester.game.integration.OneSignalManager
import com.jeweljester.game.integration.UserAgentProvider
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

        // White/Black integration. Order matters: storage before the manager (it reads
        // attributionSettled), user agent before anything can probe, OneSignal last so
        // the AppsFlyer UID can be its external id.
        IntegrationStorage.init(this)
        DeviceSignals.init(this)
        // Accelerometer sampling takes a full second, so it must begin at launch, not
        // where the URL is built.
        AccelerometerProbe.start(this)
        UserAgentProvider.init(this)
        AppsFlyerManager.init(this)
        OneSignalManager.init(this)
        // Синхронизируем настройку звука из сохранённого состояния.
        appScope.launch {
            repository.gameData
                .map { it.settings.soundEnabled }
                .collect { enabled -> SoundManager.setEnabled(enabled) }
        }
    }
}
