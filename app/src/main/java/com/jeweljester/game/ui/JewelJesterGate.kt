package com.jeweljester.game.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jeweljester.game.data.Assets
import com.jeweljester.game.integration.AdjustManager
import com.jeweljester.game.integration.DeepLinkRouter
import com.jeweljester.game.integration.IntegrationStorage
import com.jeweljester.game.integration.RouteDecision
import com.jeweljester.game.integration.TrafficRouter
import com.jeweljester.game.navigation.JewelJesterRoot
import com.jeweljester.game.ui.components.JewelBackground
import com.jeweljester.game.ui.screens.OfferWebViewScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

private const val ATTRIBUTION_TIMEOUT_MS = 8_000L

/**
 * Startup gate. Decides between White (the game) and Black (the offer WebView), with a
 * push deep link overriding both while the branch is still Black. Additive: the White
 * branch renders the existing game root.
 */
@Composable
fun JewelJesterGate() {
    var deepLinkUrl by remember { mutableStateOf<String?>(null) }
    var routeGate by remember { mutableStateOf<RouteDecision?>(null) }
    var painted by remember { mutableStateOf(false) }
    var reroutes by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        DeepLinkRouter.setHandler { url -> deepLinkUrl = url }
        onDispose { DeepLinkRouter.clearHandler() }
    }

    LaunchedEffect(reroutes) {
        routeGate = null
        painted = false
        // On a first launch the offer link is worthless until attribution lands, so wait —
        // but bounded, because Adjust may never answer and a splash that never ends is
        // worse than an unattributed offer. On a rebuild (dead cache) the decision already
        // exists, so no wait.
        if (!withContext(Dispatchers.IO) { IntegrationStorage.hasRouteDecision() }) {
            withTimeoutOrNull(ATTRIBUTION_TIMEOUT_MS + 2_000L) {
                suspendCancellableCoroutine { continuation ->
                    AdjustManager.awaitAttribution(ATTRIBUTION_TIMEOUT_MS) {
                        if (continuation.isActive) continuation.resume(Unit)
                    }
                }
            }
        }
        TrafficRouter.route { decision -> routeGate = decision }
    }

    val currentDeepLink = deepLinkUrl
    when {
        // A push is a return visit, not a new reason to ask the tracker: it never changes
        // the branch, and a White-locked device stays White.
        currentDeepLink != null && !IntegrationStorage.isWhiteLocked ->
            OfferWebViewScreen(url = currentDeepLink, decideBranch = false)

        routeGate is RouteDecision.Black -> {
            val black = routeGate as RouteDecision.Black
            Box(Modifier.fillMaxSize()) {
                // Keyed on the address AND its source: a rebuild after a dead cache is a
                // different load with different error meaning, so it needs its own WebView.
                androidx.compose.runtime.key(black.url, black.fromCache) {
                    OfferWebViewScreen(
                        url = black.url,
                        fromCache = black.fromCache,
                        decideBranch = true,
                        onWhiteLocked = {
                            IntegrationStorage.lockWhite()
                            routeGate = RouteDecision.White
                        },
                        onCacheDead = {
                            IntegrationStorage.clearOfferUrl()
                            reroutes++
                        },
                        onFreshUnavailable = { routeGate = RouteDecision.White },
                        onPainted = { url ->
                            IntegrationStorage.saveOfferUrl(url)
                            painted = true
                        },
                    )
                }
                // Until the offer actually paints, keep the splash: a White install must
                // not see a flash of the offer screen on its way to a 404.
                if (!painted) SplashContent()
            }
        }

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
