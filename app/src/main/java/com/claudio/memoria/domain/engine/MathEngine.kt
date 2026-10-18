package com.claudio.memoria.domain.engine

import kotlin.random.Random

data class MathQuestion(
    val text: String,
    val answer: Int,
    val options: List<Int>
)

object MathEngine {

    /**
     * Generates a simple mental-math question adapted to a level (1..8).
     * Higher levels use larger numbers and multiplication.
     */
    fun generate(level: Int, random: Random = Random.Default): MathQuestion {
        val lvl = level.coerceIn(1, 8)
        val q = when (lvl) {
            1 -> add(random, 1, 5, 1, 5)
            2 -> if (random.nextBoolean()) sub(random, 2, 11) else add(random, 1, 11, 1, 11)
            3 -> if (random.nextBoolean()) add(random, 5, 21, 2, 16) else sub(random, 10, 30)
            4 -> mul(random, 2, 6, 2, 6)
            5 -> if (random.nextInt(100) < 50) mul(random, 2, 10, 2, 10) else sub(random, 10, 40)
            else -> when (random.nextInt(3)) {
                0 -> mul(random, 3, 11, 2, 11)
                1 -> sub(random, 20, 80)
                else -> add(random, 20, 90, 10, 50)
            }
        }
        val text = "${q.a} ${q.op} ${q.b}"
        return MathQuestion(text, q.answer, buildOptions(q.answer, random))
    }

    fun options(answer: Int, random: Random = Random.Default): List<Int> = buildOptions(answer, random)

    private fun add(random: Random, aMin: Int, aMax: Int, bMin: Int, bMax: Int): Quad {
        val a = random.nextInt(aMin, aMax)
        val b = random.nextInt(bMin, bMax)
        return Quad(a, b, "+", a + b)
    }

    private fun sub(random: Random, aMin: Int, aMax: Int): Quad {
        val hi = random.nextInt(aMin, aMax)
        val lo = random.nextInt(1, hi + 1)
        return Quad(hi, lo, "-", hi - lo)
    }

    private fun mul(random: Random, aMin: Int, aMax: Int, bMin: Int, bMax: Int): Quad {
        val a = random.nextInt(aMin, aMax)
        val b = random.nextInt(bMin, bMax)
        return Quad(a, b, "x", a * b)
    }

    private fun buildOptions(answer: Int, random: Random): List<Int> {
        val options = linkedSetOf(answer)
        var guard = 0
        while (options.size < 4 && guard < 80) {
            val delta = random.nextInt(1, 6) * (if (random.nextBoolean()) 1 else -1)
            val candidate = answer + delta
            if (candidate >= 0 && candidate != answer) options.add(candidate)
            guard++
        }
        var filler = answer + 7
        while (options.size < 4) { options.add(filler); filler += 3 }
        return options.toList().shuffled(random)
    }

    private data class Quad(val a: Int, val b: Int, val op: String, val answer: Int)
}
