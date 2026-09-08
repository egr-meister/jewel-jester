package com.jeweljester.game.integration

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfferUrlBuilderTest {

    @Test
    fun buildsFullUrlWithSub1InPathAndQuery() {
        val url = OfferUrlBuilder.build(
            baseUrl = "https://secret-joker.cfd/",
            appsFlyerParams = linkedMapOf(
                "af_status" to "Non-organic",
                "media_source" to "facebook",
                "campaign" to "android1_AR_sub3%26_sub4_WBZ_sub6"
            ),
            appsFlyerId = "test-af-id"
        )

        assertTrue(url.startsWith("https://secret-joker.cfd/android1?"))
        assertTrue(url.contains("af_status=Non-organic"))
        assertTrue(url.contains("media_source=facebook"))
        assertTrue(url.contains("campaign=android1_AR_sub3%26_sub4_WBZ_sub6"))
        assertTrue(url.contains("sub1=android1"))
        assertTrue(url.contains("sub2=AR"))
        assertTrue(url.contains("sub3=sub3%26"))
        assertTrue(url.contains("sub4=sub4"))
        assertTrue(url.contains("sub5=WBZ"))
        assertTrue(url.contains("sub6=sub6"))
        assertTrue(url.contains("appsflyer_id=test-af-id"))
        assertFalse(url.contains("%2526")) // no double-encoding
    }

    @Test
    fun emptySub1LeavesPathUnchanged() {
        val url = OfferUrlBuilder.build(
            baseUrl = "https://secret-joker.cfd/",
            appsFlyerParams = linkedMapOf("af_status" to "Organic"),
            appsFlyerId = null
        )
        assertTrue(url.startsWith("https://secret-joker.cfd/?"))
        assertTrue(url.contains("af_status=Organic"))
        assertFalse(url.contains("appsflyer_id"))
    }

    @Test
    fun keepsExistingBasePathBeforeSub1() {
        val url = OfferUrlBuilder.build(
            baseUrl = "https://domain.com/offer/",
            appsFlyerParams = linkedMapOf("campaign" to "test_AA"),
            appsFlyerId = null
        )
        assertTrue(url.startsWith("https://domain.com/offer/test?"))
    }

    @Test
    fun doesNotAddSub1ToPathTwice() {
        val first = OfferUrlBuilder.build(
            baseUrl = "https://domain.com/",
            appsFlyerParams = linkedMapOf("campaign" to "android1_AR"),
            appsFlyerId = null
        )
        val again = OfferUrlBuilder.build(
            baseUrl = first,
            appsFlyerParams = linkedMapOf("campaign" to "android1_AR"),
            appsFlyerId = null
        )
        assertFalse(again.contains("/android1/android1"))
        assertTrue(again.startsWith("https://domain.com/android1?"))
    }

    @Test
    fun blankBaseUrlYieldsEmpty() {
        assertTrue(OfferUrlBuilder.build("", emptyMap(), null).isEmpty())
    }
}
