package com.jeweljester.game

import com.jeweljester.game.data.GameLogic
import com.jeweljester.game.data.PairsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameLogicTest {

    private val pool = (1..12).toList()

    @Test
    fun buildDeck_hasTwoOfEachChosenItem() {
        val deck = GameLogic.buildDeck(pairs = 4, pool = pool, rng = Random(42))
        assertEquals(8, deck.size)
        val counts = deck.groupingBy { it }.eachCount()
        assertEquals(4, counts.size)
        assertTrue(counts.values.all { it == 2 })
    }

    @Test
    fun buildDeck_maxPairsUsesWholePool() {
        val deck = GameLogic.buildDeck(pairs = 12, pool = pool, rng = Random(1))
        assertEquals(24, deck.size)
        assertEquals(12, deck.toSet().size)
    }

    @Test
    fun firstLevelAlwaysUnlocked() {
        assertTrue(GameLogic.isLevelUnlocked(emptyMap(), 1))
    }

    @Test
    fun secondLevelLockedUntilFirstCompleted() {
        assertFalse(GameLogic.isLevelUnlocked(emptyMap(), 2))
        val need1 = PairsData.byLevel(1).pairs
        val progress = mapOf("1" to need1)
        assertTrue(GameLogic.isLevelCompleted(progress, 1))
        assertTrue(GameLogic.isLevelUnlocked(progress, 2))
    }

    @Test
    fun partialProgressDoesNotCompleteLevel() {
        val need3 = PairsData.byLevel(3).pairs
        val progress = mapOf("3" to need3 - 1)
        assertFalse(GameLogic.isLevelCompleted(progress, 3))
    }

    @Test
    fun pairsCompletedCount_countsOnlyFullyCleared() {
        val progress = mapOf(
            "1" to PairsData.byLevel(1).pairs,
            "2" to PairsData.byLevel(2).pairs,
            "3" to 1 // не пройден
        )
        assertEquals(2, GameLogic.pairsCompletedCount(progress))
    }
}
