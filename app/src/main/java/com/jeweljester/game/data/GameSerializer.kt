package com.jeweljester.game.data

import com.jeweljester.game.data.model.GameData
import kotlinx.serialization.json.Json

/**
 * Безопасная (де)сериализация состояния игры. Вынесено отдельно от
 * репозитория, чтобы не зависеть от Android Context и легко тестироваться.
 */
object GameSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Пустая/битая/отсутствующая строка -> дефолтная модель, без исключений. */
    fun decode(raw: String?): GameData {
        if (raw.isNullOrBlank()) return GameData()
        return try {
            json.decodeFromString(GameData.serializer(), raw)
        } catch (t: Throwable) {
            GameData()
        }
    }

    fun encode(data: GameData): String =
        json.encodeToString(GameData.serializer(), data)
}
