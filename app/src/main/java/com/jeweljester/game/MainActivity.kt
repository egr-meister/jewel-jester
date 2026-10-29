package com.jeweljester.game

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeweljester.game.integration.DeepLinkRouter
import com.jeweljester.game.integration.PushPayload
import com.jeweljester.game.ui.JewelJesterGate
import com.jeweljester.game.ui.theme.JewelDeep
import com.jeweljester.game.ui.theme.JewelJesterTheme
import com.jeweljester.game.ui.theme.ThemeMode
import kotlinx.coroutines.flow.map

// AppCompatActivity (not ComponentActivity): required for AppCompatDelegate.setApplicationLocales
// to apply and survive a restart below API 33 (see LocaleManager / the manifest service).
class MainActivity : AppCompatActivity() {

    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result ignored */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // A deep link can arrive on a cold start by tapping a notification.
        consumeIntent(intent)
        requestNotificationsPermission()

        val repository = (application as JewelJesterApp).repository
        setContent {
            val themeName by repository.gameData
                .map { it.settings.themeMode }
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM.name)
            JewelJesterTheme(ThemeMode.from(themeName)) {
                Surface(modifier = Modifier.fillMaxSize(), color = JewelDeep) {
                    JewelJesterGate()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // singleTop: a push into an already-running app lands here, not in a new instance.
        setIntent(intent)
        consumeIntent(intent)
    }

    private fun consumeIntent(intent: Intent?) {
        if (intent == null) return
        // Marked on the intent itself, not a field, so one push is not opened twice when the
        // Activity is recreated for a reason other than rotation.
        if (intent.getBooleanExtra(EXTRA_CONSUMED, false)) return
        // A plain launcher intent is not a push: only act on our own deep-link extra.
        val url = intent.getStringExtra(PushPayload.KEY_URL)
        if (url.isNullOrBlank()) return
        intent.putExtra(EXTRA_CONSUMED, true)
        DeepLinkRouter.handle(url)
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        // Fire-and-forget: the token arrives without the permission, so routing and
        // push_token in the click do not depend on it. A refusal must break nothing.
        if (!granted) requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private companion object {
        const val EXTRA_CONSUMED = "jewel_push_consumed"
    }
}
