package com.jeweljester.game.integration

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * GAID (`gps_adid`) is the primary key Adjust S2S postbacks and the offer's partner match
 * conversions on for Android — a different id from Adjust's `adid`. It must ride the single
 * click, always. Reading it is blocking and talks to Google Play Services, so it is read
 * ahead of time on a background thread from Application.onCreate, with its own latch — just
 * like [PushManager] and [AccelerometerProbe].
 */
object GaidProbe {

    private const val AWAIT_TIMEOUT_SECONDS = 3L
    private const val ZEROED = "00000000-0000-0000-0000-000000000000"

    private val started = AtomicBoolean(false)
    private val ready = CountDownLatch(1)

    /** Called from Application.onCreate: the GAID read blocks, so start it early. */
    fun start(context: Context) {
        if (!started.compareAndSet(false, true)) return
        // An already-read GAID answers at once: later launches wait for nothing.
        if (!IntegrationStorage.gpsAdid.isNullOrBlank()) {
            ready.countDown()
            return
        }
        val app = context.applicationContext
        Thread {
            runCatching {
                val info = AdvertisingIdClient.getAdvertisingIdInfo(app)
                val id = info.id
                // Limit-ad-tracking on, or a zeroed id, is not a key: store nothing, but the
                // key still goes out empty (so "no GAID" is distinct from "not in time").
                if (!info.isLimitAdTrackingEnabled && !id.isNullOrBlank() && id != ZEROED) {
                    IntegrationStorage.gpsAdid = id
                }
            }
            // Open the latch on failure too: waiting for the GAID any longer is pointless.
            ready.countDown()
        }.start()
    }

    /** Background threads only: blocks up to a few seconds for the first read. */
    fun await(): String {
        if (!started.get()) return IntegrationStorage.gpsAdid.orEmpty()
        runCatching { ready.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS) }
        return IntegrationStorage.gpsAdid.orEmpty()
    }
}
