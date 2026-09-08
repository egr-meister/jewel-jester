package com.jeweljester.game.integration

import android.app.Application
import android.util.Log
import com.jeweljester.game.BuildConfig
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * OneSignal push. Initialized strictly AFTER AppsFlyerManager so the AppsFlyer UID can be
 * used as OneSignal's external id. A blank ONESIGNAL_APP_ID disables push cleanly, so the
 * app still builds and runs before the id is provided.
 */
object OneSignalManager {

    private const val TAG = "OneSignalManager"

    @Volatile
    private var initialized = false

    fun init(app: Application) {
        val appId = BuildConfig.ONESIGNAL_APP_ID
        if (appId.isBlank()) {
            Log.w(TAG, "ONESIGNAL_APP_ID is empty - push disabled for this build")
            return
        }

        OneSignal.initWithContext(app, appId)
        initialized = true

        // A tapped push may carry a deep link in additionalData.url - route it into the app.
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val url = event.notification.additionalData?.optString("url").orEmpty()
                if (url.isNotBlank()) DeepLinkRouter.handle(url)
            }
        })

        // OneSignal 5.x requestPermission is a suspend function - call it from a coroutine.
        CoroutineScope(Dispatchers.Main).launch {
            runCatching { OneSignal.Notifications.requestPermission(true) }
                .onFailure { Log.w(TAG, "requestPermission failed", it) }
        }

        // If the AppsFlyer UID was already captured before OneSignal came up, sync now.
        syncExternalId()
    }

    /** external_id = AppsFlyer ID. No-op until OneSignal is initialized and a UID exists. */
    fun syncExternalId() {
        if (!initialized) return
        val id = IntegrationStorage.appsFlyerId
        if (!id.isNullOrBlank()) {
            runCatching { OneSignal.login(id) }
                .onFailure { Log.w(TAG, "OneSignal.login failed", it) }
        }
    }
}
