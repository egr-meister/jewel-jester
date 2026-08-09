package com.jeweljester.game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jeweljester.game.data.model.GameData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jewel_jester")

/**
 * Единственный локальный репозиторий. Хранит весь прогресс в DataStore
 * Preferences как один JSON-стринг (Kotlinx Serialization).
 *
 * Все операции чтения безопасны: пустое хранилище, отсутствующий ключ,
 * пустой или повреждённый JSON приводят к значению по умолчанию, а не к падению.
 */
class GameRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    private val gameKey = stringPreferencesKey("game_json")

    /** Наблюдаемое состояние игры. */
    val gameData: Flow<GameData> = store.data
        .catch { emit(emptyPreferences()) } // защита от ошибок чтения DataStore
        .map { prefs -> GameSerializer.decode(prefs[gameKey]) }

    private suspend fun update(transform: (GameData) -> GameData) {
        store.edit { prefs ->
            val current = GameSerializer.decode(prefs[gameKey])
            prefs[gameKey] = GameSerializer.encode(transform(current))
        }
    }

    /** Сохранить лучший результат викторины по категории. */
    suspend fun saveQuizScore(categoryId: String, score: Int) = update { data ->
        val best = data.quizBest[categoryId] ?: 0
        if (score > best) data.copy(quizBest = data.quizBest + (categoryId to score)) else data
    }

    /** Сохранить лучший результат уровня «найди пару». */
    suspend fun savePairsResult(level: Int, matchedPairs: Int) = update { data ->
        val key = level.toString()
        val best = data.pairsBest[key] ?: 0
        if (matchedPairs > best) data.copy(pairsBest = data.pairsBest + (key to matchedPairs)) else data
    }

    suspend fun setSoundEnabled(enabled: Boolean) = update { data ->
        data.copy(settings = data.settings.copy(soundEnabled = enabled))
    }

    /** Полный сброс локального прогресса. */
    suspend fun resetAll() {
        store.edit { it.remove(gameKey) }
    }
}
