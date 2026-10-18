package com.claudio.memoria.domain

import com.claudio.memoria.domain.engine.MathEngine
import com.claudio.memoria.domain.engine.PairsEngine
import com.claudio.memoria.domain.engine.SequenceEngine
import com.claudio.memoria.domain.engine.SeriesEngine
import com.claudio.memoria.domain.engine.VisualMemoryEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EngineTest {

    private val symbols = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l")

    @Test
    fun `math questions always have four options containing the answer`() {
        val random = Random(42)
        for (level in 1..8) {
            repeat(200) {
                val question = MathEngine.generate(level, random)
                assertEquals(4, question.options.size)
                assertTrue(question.options.contains(question.answer))
                assertTrue(question.options.all { it >= 0 })
                assertTrue(question.options.distinct().size == 4)
                assertTrue(question.text.isNotBlank())
            }
        }
    }

    @Test
    fun `series questions hide exactly one element and include the answer`() {
        val random = Random(7)
        for (level in 1..8) {
            repeat(200) {
                val question = SeriesEngine.generate(level, random)
                assertEquals(1, question.items.count { it == null })
                assertEquals(null, question.items[question.missingIndex])
                assertEquals(4, question.options.size)
                assertTrue(question.options.contains(question.answer))
                assertTrue(question.items.size >= 5)
            }
        }
    }

    @Test
    fun `sequence length grows with level and respects palette`() {
        val random = Random(11)
        for (level in 1..8) {
            repeat(50) {
                val sequence = SequenceEngine.generate(level, 4, random)
                assertEquals(SequenceEngine.lengthFor(level), sequence.size)
                assertTrue(sequence.all { it in 0..3 })
            }
        }
    }

    @Test
    fun `visual memory places the expected number of distinct items`() {
        val random = Random(3)
        for (level in 1..8) {
            repeat(50) {
                val round = VisualMemoryEngine.generate(level, symbols, random)
                val cells = round.gridSize * round.gridSize
                assertEquals(round.items.size, round.targetCells.size)
                assertTrue(round.items.keys.all { it in 0 until cells })
                assertTrue(round.items.size <= 8)
                round.items.values.forEach { assertNotNull(it) }
            }
        }
    }

    @Test
    fun `pairs deck has two of each symbol and respects the pool`() {
        val random = Random(5)
        for (level in 1..8) {
            repeat(50) {
                val deck = PairsEngine.generateDeck(level, symbols, random)
                val expectedPairs = PairsEngine.pairCountFor(level).coerceAtMost(symbols.size)
                assertEquals(expectedPairs * 2, deck.size)
                val grouped = deck.groupBy { it.pairId }
                assertEquals(expectedPairs, grouped.size)
                assertTrue(grouped.values.all { cards -> cards.size == 2 })
                assertTrue(deck.all { it.symbol in symbols })
            }
        }
    }
}
