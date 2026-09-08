package com.jeweljester.game.integration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlSchemesTest {

    @Test
    fun httpAndHttpsStayInternal() {
        assertFalse(UrlSchemes.isExternal("https://example.com/x"))
        assertFalse(UrlSchemes.isExternal("http://example.com/x"))
    }

    @Test
    fun relativeUrlStaysInternal() {
        assertFalse(UrlSchemes.isExternal("/path/page"))
        assertNull(UrlSchemes.schemeOf("/path:with:colons"))
    }

    @Test
    fun customSchemesAreExternal() {
        assertTrue(UrlSchemes.isExternal("intent://pay#Intent;end"))
        assertTrue(UrlSchemes.isExternal("tg://resolve?domain=x"))
        assertTrue(UrlSchemes.isExternal("market://details?id=x"))
    }

    @Test
    fun allowedNonHttpSchemesStayInternal() {
        assertFalse(UrlSchemes.isExternal("about:blank"))
        assertFalse(UrlSchemes.isExternal("data:text/html,hi"))
        assertFalse(UrlSchemes.isExternal("blob:https://x/abc"))
    }

    @Test
    fun schemeIsLowercased() {
        assertEquals("https", UrlSchemes.schemeOf("HTTPS://X"))
    }
}
