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

    /** Distinct note per colour, used by the "recordar secuencias" game. */
    fun color(index: Int) = play(COLOR_TONES[index.mod(COLOR_TONES.size)], 260)

    fun release() {
        runCatching { toneGenerator?.release() }
        toneGenerator = null
    }

    private companion object {
        const val VOLUME = 70

        val COLOR_TONES = intArrayOf(
            ToneGenerator.TONE_DTMF_1,
            ToneGenerator.TONE_DTMF_2,
            ToneGenerator.TONE_DTMF_3,
            ToneGenerator.TONE_DTMF_4,
            ToneGenerator.TONE_DTMF_5,
            ToneGenerator.TONE_DTMF_6
        )
    }
}
