package com.claudio.memoria.ui.sound

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Very small feedback sounds using the system ToneGenerator: no audio assets and
 * no extra dependency. Respects the user's sound preference at play time.
 */
class SoundEffects(private val enabled: () -> Boolean) {

    private var toneGenerator: ToneGenerator? = null

    private fun play(tone: Int, durationMs: Int) {
        if (!enabled()) return
        runCatching {
            val generator = toneGenerator
                ?: ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME).also { toneGenerator = it }
            generator.startTone(tone, durationMs)
        }
    }

    fun success() = play(ToneGenerator.TONE_PROP_ACK, 140)

    fun error() = play(ToneGenerator.TONE_PROP_NACK, 170)

    fun tap() = play(ToneGenerator.TONE_PROP_BEEP, 60)

    fun release() {
        runCatching { toneGenerator?.release() }
        toneGenerator = null
    }

    private companion object {
        const val VOLUME = 70
    }
}
