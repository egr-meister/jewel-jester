package com.jeweljester.game

import com.jeweljester.game.data.GameSerializer
import com.jeweljester.game.data.model.GameData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSerializerTest {

    @Test
    fun decodeNull_returnsDefault() {
        val data = GameSerializer.decode(null)
        assertTrue(data.quizBest.isEmpty())
        assertTrue(data.pairsBest.isEmpty())
        assertTrue(data.settings.soundEnabled)
    }

    @Test
    fun decodeEmpty_returnsDefault() {
        assertEquals(GameData(), GameSerializer.decode(""))
        assertEquals(GameData(), GameSerializer.decode("   "))
    }

    @Test
    fun decodeCorruptedJson_returnsDefaultInsteadOfCrashing() {
        assertEquals(GameData(), GameSerializer.decode("{ this is not json"))
        assertEquals(GameData(), GameSerializer.decode("[1,2,3]"))
    }

    @Test
    fun encodeThenDecode_roundTrips() {
        val original = GameData(
            quizBest = mapOf("gem_master" to 8),
            pairsBest = mapOf("1" to 2, "2" to 3)
        )
        val restored = GameSerializer.decode(GameSerializer.encode(original))
        assertEquals(original, restored)
    }

    @Test
    fun decodeUnknownKeys_ignoredForForwardCompatibility() {
        val json = """{"quizBest":{"gem_master":5},"pairsBest":{},"settings":{"soundEnabled":false},"futureField":123}"""
        val data = GameSerializer.decode(json)
        assertEquals(5, data.quizBest["gem_master"])
        assertEquals(false, data.settings.soundEnabled)
    }

    @Test
    fun decodeMissingFields_usesDefaults() {
        // старый JSON без settings
        val json = """{"quizBest":{"gem_master":3}}"""
        val data = GameSerializer.decode(json)
        assertEquals(3, data.quizBest["gem_master"])
        assertTrue(data.settings.soundEnabled)
        assertTrue(data.pairsBest.isEmpty())
    }
}
