package com.claudio.memoria.domain.engine

import kotlin.random.Random

data class SeriesQuestion(
    val items: List<Int?>,
    val missingIndex: Int,
    val answer: Int,
    val options: List<Int>
)

object SeriesEngine {

    /** Builds a "what comes next / which is missing" number-series challenge. */
    fun generate(level: Int, random: Random = Random.Default): SeriesQuestion {
        val lvl = level.coerceIn(1, 8)
        val seq: List<Int> = when (lvl) {
            1 -> arithmetic(random.nextInt(1, 6), random.nextInt(2, 5), 5)
            2 -> if (random.nextBoolean()) arithmetic(random.nextInt(1, 9), random.nextInt(2, 5), 5)
            else arithmetic(random.nextInt(10, 30), random.nextInt(2, 6), 5, descending = true)
            3 -> arithmetic(random.nextInt(2, 9), random.nextInt(3, 8), 5)
            4 -> geometric(random.nextInt(1, 4), 2, 5)
            5 -> alternating(random.nextInt(1, 6), random.nextInt(2, 4), random.nextInt(3, 7), 6)
            6 -> if (random.nextBoolean()) geometric(random.nextInt(1, 4), 3, 5) else arithmetic(random.nextInt(3, 12), random.nextInt(4, 10), 5)
            7 -> fibonacci(random.nextInt(1, 4), random.nextInt(2, 5), 6)
            else -> alternating(random.nextInt(2, 9), random.nextInt(3, 8), random.nextInt(4, 10), 6)
        }

        val missingIndex = random.nextInt(1, seq.size)
        val answer = seq[missingIndex]
        val items = seq.mapIndexed { i, v -> if (i == missingIndex) null else v }
        return SeriesQuestion(items, missingIndex, answer, options(answer, random))
    }

    fun options(answer: Int, random: Random = Random.Default): List<Int> {
        val opts = linkedSetOf(answer)
        var guard = 0
        while (opts.size < 4 && guard < 80) {
            val delta = random.nextInt(1, 5) * (if (random.nextBoolean()) 1 else -1)
            val c = answer + delta
            if (c >= 0 && c != answer) opts.add(c)
            guard++
        }
        var filler = answer + 6
        while (opts.size < 4) { opts.add(filler); filler += 2 }
        return opts.toList().shuffled(random)
    }

    private fun arithmetic(start: Int, step: Int, length: Int, descending: Boolean = false): List<Int> {
        var current = if (descending) start.coerceAtLeast(step * (length - 1) + 1) else start
        return buildList {
            repeat(length) {
                add(current)
                current += if (descending) -step else step
            }
        }
    }

    private fun geometric(start: Int, factor: Int, length: Int): List<Int> {
        var current = start
        return buildList {
            repeat(length) {
                add(current)
                current *= factor
            }
        }
    }

    private fun alternating(start: Int, stepA: Int, stepB: Int, length: Int): List<Int> {
        var current = start
        return buildList {
            repeat(length) { i ->
                add(current)
                current += if (i % 2 == 0) stepA else stepB
            }
        }
    }

    private fun fibonacci(a: Int, b: Int, length: Int): List<Int> {
        var prev = a
        var curr = b
        return buildList {
            add(prev)
            add(curr)
            repeat(length - 2) {
                val next = prev + curr
                add(next)
                prev = curr
                curr = next
            }
        }
    }
}
