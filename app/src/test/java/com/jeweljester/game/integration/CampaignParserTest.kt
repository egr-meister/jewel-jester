package com.jeweljester.game.integration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignParserTest {

    @Test
    fun parsesPositionalSegmentsWithoutDecoding() {
        val subs = CampaignParser.parse("android1_AR_sub3%26_sub4_WBZ_sub6")
        assertEquals("android1", subs["sub1"])
        assertEquals("AR", subs["sub2"])
        assertEquals("sub3%26", subs["sub3"]) // stays encoded
        assertEquals("sub4", subs["sub4"])
        assertEquals("WBZ", subs["sub5"])
        assertEquals("sub6", subs["sub6"])
        assertEquals(6, subs.size)
    }

    @Test
    fun emptyCampaignYieldsNoSubs() {
        assertTrue(CampaignParser.parse("").isEmpty())
    }

    @Test
    fun emptySegmentsAreKept() {
        val subs = CampaignParser.parse("a__c")
        assertEquals("a", subs["sub1"])
        assertEquals("", subs["sub2"])
        assertEquals("c", subs["sub3"])
    }

    @Test
    fun toQueryDoesNotDoubleEncode() {
        val q = CampaignParser.toQuery(linkedMapOf("sub3" to "sub3%26"))
        assertEquals("sub3=sub3%26", q)
        assertTrue(!q.contains("%2526"))
    }
}
