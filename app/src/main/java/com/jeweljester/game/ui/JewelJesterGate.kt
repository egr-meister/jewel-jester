package com.jeweljester.game.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jeweljester.game.data.Assets
import com.jeweljester.game.integration.AppsFlyerManager
import com.jeweljester.game.integration.DeepLinkRouter
import com.jeweljester.game.integration.IntegrationStorage
import com.jeweljester.game.integration.RouteDecision
import com.jeweljester.game.integration.TrafficRouter
import com.jeweljester.game.navigation.JewelJesterRoot
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.screens.OfferWebViewScreen
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

private const val ATTRIBUTION_TIMEOUT_MS = 8_000L

/**
 * Startup gate. Decides between White (the game) and Black (the offer WebView), with a
 * deep link overriding both. Additive: the White branch renders the existing game root.
 */
@Composable
fun JewelJesterGate() {
    var deepLinkUrl by remember { mutableStateOf<String?>(null) }
    var routeGate by remember { mutableStateOf<RouteDecision?>(null) }

    DisposableEffect(Unit) {
        DeepLinkRouter.setHandler { url -> deepLinkUrl = url }
        onDispose { DeepLinkRouter.clearHandler() }
    }

    LaunchedEffect(Unit) {
        // On a first launch the offer URL is worthless until conversion data lands, so
        // wait - but bounded, because AppsFlyer may never answer and a splash that never
        // ends is worse than an unattributed offer.
        if (!IntegrationStorage.hasRouteDecision) {
            withTimeoutOrNull(ATTRIBUTION_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    AppsFlyerManager.setOnAttributionResolved {
                        if (continuation.isActive) continuation.resume(Unit)
                    }
                    continuation.invokeOnCancellation {
                        AppsFlyerManager.setOnAttributionResolved(null)
                    }
                }
            }
            AppsFlyerManager.setOnAttributionResolved(null)
        }
        TrafficRouter.route { decision -> routeGate = decision }
    }

    val currentDeepLink = deepLinkUrl
    when {
        currentDeepLink != null -> OfferWebViewScreen(currentDeepLink)
        routeGate is RouteDecision.Black -> OfferWebViewScreen((routeGate as RouteDecision.Black).url)
        routeGate is RouteDecision.White -> JewelJesterRoot() // existing game
        else -> SplashContent()
    }
}

@Composable
private fun SplashContent() {
    JewelBackground {
        Image(
            painter = painterResource(id = Assets.logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f)
                .height(180.dp)
        )
    }
}
