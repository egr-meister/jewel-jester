package com.jeweljester.game.data.model

import kotlinx.serialization.Serializable

/**
 * Всё сохраняемое состояние игры в одном сериализуемом объекте.
 * Все поля имеют значения по умолчанию — это гарантирует безопасную
 * десериализацию старого/неполного JSON после обновлений модели.
 *
 * quizBest  : categoryId -> лучший результат (0..10)
 * pairsBest : номер уровня (строкой) -> лучшее число собранных пар
 */
@Serializable
data class GameData(
    val quizBest: Map<String, Int> = emptyMap(),
    val pairsBest: Map<String, Int> = emptyMap(),
    val settings: Settings = Settings()
)

@Serializable
data class Settings(
    val soundEnabled: Boolean = true,
    // SYSTEM | LIGHT | DARK. Language is NOT stored here — AppCompatDelegate is the source
    // of truth for the locale (see LocaleManager), so a second copy cannot drift.
    val themeMode: String = "SYSTEM"
)
