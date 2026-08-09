package com.jeweljester.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.jeweljester.game.R

/**
 * Простой менеджер коротких звуков на SoundPool. Инициализируется один раз в
 * Application. Уважает настройку звука (setEnabled). Полностью локальный.
 */
object SoundManager {

    private var pool: SoundPool? = null
    private val ids = HashMap<Int, Int>()
    @Volatile private var enabled = true

    private val effects = listOf(
        R.raw.click, R.raw.correct, R.raw.wrong, R.raw.match, R.raw.win, R.raw.lose
    )

    fun init(context: Context) {
        if (pool != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val sp = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
        val app = context.applicationContext
        effects.forEach { res -> ids[res] = sp.load(app, res, 1) }
        pool = sp
    }

    fun setEnabled(value: Boolean) { enabled = value }

    private fun play(res: Int) {
        if (!enabled) return
        val sp = pool ?: return
        val id = ids[res] ?: return
        sp.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun click() = play(R.raw.click)
    fun correct() = play(R.raw.correct)
    fun wrong() = play(R.raw.wrong)
    fun match() = play(R.raw.match)
    fun win() = play(R.raw.win)
    fun lose() = play(R.raw.lose)
}
