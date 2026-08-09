package com.jeweljester.game.data

/**
 * Настройки уровней «найди пару». Сложность растёт от 2 до 12 пар.
 * pairs — число пар, columns — колонок в сетке, seconds — время на уровень.
 */
data class LevelConfig(
    val level: Int,
    val pairs: Int,
    val columns: Int,
    val seconds: Int
)

object PairsData {
    val levels: List<LevelConfig> = listOf(
        LevelConfig(1, 2, 2, 60),
        LevelConfig(2, 3, 2, 70),
        LevelConfig(3, 4, 2, 70),
        LevelConfig(4, 6, 3, 80),
        LevelConfig(5, 6, 3, 80),
        LevelConfig(6, 8, 4, 100),
        LevelConfig(7, 8, 4, 100),
        LevelConfig(8, 10, 4, 110),
        LevelConfig(9, 12, 4, 120),
    )

    fun byLevel(level: Int): LevelConfig =
        levels.firstOrNull { it.level == level } ?: levels.first()

    const val TOTAL_LEVELS = 9
}
