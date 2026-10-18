package com.claudio.memoria.domain.engine

import kotlin.random.Random

object SequenceEngine {

    /** Length of the colour sequence to reproduce at a given level. */
    fun lengthFor(level: Int): Int = (level + 2).coerceIn(3, 12)

    fun generate(level: Int, paletteSize: Int, random: Random = Random.Default): List<Int> {
        val length = lengthFor(level)
        val palette = paletteSize.coerceAtLeast(2)
        return List(length) { random.nextInt(palette) }
    }
}
