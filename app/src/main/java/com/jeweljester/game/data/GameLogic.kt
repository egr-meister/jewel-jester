package com.jeweljester.game.data

import kotlin.random.Random

/**
 * Чистые функции игровой логики — без Android-зависимостей, легко тестируются.
 */
object GameLogic {

    /** Уровень пройден, если собрано не меньше пар, чем задано в конфиге. */
    fun isLevelCompleted(pairsBest: Map<String, Int>, level: Int): Boolean {
        val need = PairsData.byLevel(level).pairs
        return (pairsBest[level.toString()] ?: 0) >= need
    }

    /** Уровень доступен, если это 1-й или предыдущий пройден. */
    fun isLevelUnlocked(pairsBest: Map<String, Int>, level: Int): Boolean =
        level <= 1 || isLevelCompleted(pairsBest, level - 1)

    /** Сколько уровней «найди пару» пройдено (для сводки, например 9/9). */
    fun pairsCompletedCount(pairsBest: Map<String, Int>): Int =
        PairsData.levels.count { isLevelCompleted(pairsBest, it.level) }

    /**
     * Собрать колоду карт: выбрать [pairs] элементов из пула, продублировать,
     * перемешать. Возвращает список идентификаторов (drawable id или индексов).
     */
    fun buildDeck(pairs: Int, pool: List<Int>, rng: Random = Random.Default): List<Int> {
        require(pairs <= pool.size) { "Not enough items in pool for $pairs pairs" }
        val chosen = pool.shuffled(rng).take(pairs)
        return (chosen + chosen).shuffled(rng)
    }
}
