package com.jeweljester.game.integration

import org.junit.Assert.assertEquals
import org.junit.Test

class DeepLinkRouterTest {

    // DeepLinkRouter is a singleton; drain any queued/registered state before each case.
    private fun drain() {
        DeepLinkRouter.setHandler { }
        DeepLinkRouter.clearHandler()
    }

    @Test
    fun deliversImmediatelyWhenHandlerIsSet() {
        drain()
        val got = mutableListOf<String>()
        DeepLinkRouter.setHandler { got.add(it) }
        DeepLinkRouter.handle("https://x/1")
        assertEquals(listOf("https://x/1"), got)
        DeepLinkRouter.clearHandler()
    }

    @Test
    fun queuesUntilHandlerRegisters() {
        drain()
        DeepLinkRouter.handle("https://x/2")
        val got = mutableListOf<String>()
        DeepLinkRouter.setHandler { got.add(it) }
        assertEquals(listOf("https://x/2"), got)
        DeepLinkRouter.clearHandler()
    }

    @Test
    fun ignoresBlankLinks() {
        drain()
        val got = mutableListOf<String>()
        DeepLinkRouter.setHandler { got.add(it) }
        DeepLinkRouter.handle(null)
        DeepLinkRouter.handle("")
        assertEquals(emptyList<String>(), got)
        DeepLinkRouter.clearHandler()
    }
}
