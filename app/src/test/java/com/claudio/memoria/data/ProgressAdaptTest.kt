package com.claudio.memoria.data

import com.claudio.memoria.data.db.GameResultEntity
import com.claudio.memoria.data.repository.ProgressRepository
import com.claudio.memoria.data.repository.accuracy
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressAdaptTest {

    @Test
    fun `accuracy is safe when total is zero`() {
        val result = GameResultEntity(
            gameId = "math",
            score = 0,
            correct = 0,
            total = 0,
            durationMs = 0,
            level = 1,
            timestamp = 0,
            dayKey = "2026-10-18"
        )
        assertEquals(0.0, result.accuracy(), 0.0001)
    }

    @Test
    fun `level goes up with good accuracy and caps`() {
        assertEquals(4, ProgressRepository.adaptLevel(3, 0.9))
        assertEquals(8, ProgressRepository.adaptLevel(8, 1.0))
    }

    @Test
    fun `level goes down with poor accuracy and floors`() {
        assertEquals(2, ProgressRepository.adaptLevel(3, 0.2))
        assertEquals(1, ProgressRepository.adaptLevel(1, 0.0))
    }

    @Test
    fun `level stays the same with medium accuracy`() {
        assertEquals(4, ProgressRepository.adaptLevel(4, 0.6))
    }
}
