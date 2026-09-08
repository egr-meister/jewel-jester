package com.jeweljester.game.integration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutingRulesTest {

    @Test
    fun notFoundMeansWhite() {
        val decision = RoutingRules.decide(ProbeResult(404, "https://x/"), "https://x/")
        assertTrue(decision is RouteDecision.White)
    }

    @Test
    fun okMeansBlackWithFinalUrl() {
        val decision = RoutingRules.decide(ProbeResult(200, "https://final/"), "https://req/")
        assertEquals(RouteDecision.Black("https://final/"), decision)
    }

    @Test
    fun blackFallsBackToRequestedWhenNoFinalUrl() {
        val decision = RoutingRules.decide(ProbeResult(500, null), "https://req/")
        assertEquals(RouteDecision.Black("https://req/"), decision)
    }

    @Test
    fun redirectStatusIsBlack() {
        val decision = RoutingRules.decide(ProbeResult(302, "https://dest/"), "https://req/")
        assertEquals(RouteDecision.Black("https://dest/"), decision)
    }
}
