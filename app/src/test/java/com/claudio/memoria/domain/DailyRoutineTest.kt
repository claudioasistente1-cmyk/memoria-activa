package com.claudio.memoria.domain

import com.claudio.memoria.domain.daily.DailyRoutineGenerator
import com.claudio.memoria.domain.model.GameType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyRoutineTest {

    private val date = LocalDate.of(2026, 10, 18)

    @Test
    fun `routine is deterministic for the same day`() {
        val first = DailyRoutineGenerator.generate(date)
        val second = DailyRoutineGenerator.generate(date)
        assertEquals(
            first.tasks.map { it.gameType },
            second.tasks.map { it.gameType }
        )
    }

    @Test
    fun `routine contains three distinct games`() {
        val routine = DailyRoutineGenerator.generate(date)
        assertEquals(3, routine.tasks.size)
        assertEquals(3, routine.tasks.map { it.gameType }.distinct().size)
    }

    @Test
    fun `routine applies suggested levels and completion`() {
        val levels = mapOf(GameType.PAIRS to 4, GameType.MATH to 2)
        val completed = setOf(GameType.MATH)
        val routine = DailyRoutineGenerator.generate(date, levels, completed)

        routine.tasks.forEach { task ->
            if (task.gameType == GameType.PAIRS) assertEquals(4, task.level)
            if (task.gameType == GameType.MATH) {
                assertEquals(2, task.level)
                assertTrue(task.completed)
            }
            if (task.gameType != GameType.MATH) assertFalse(task.completed)
        }
    }

    @Test
    fun `routine is complete only when all tasks are done`() {
        val routine = DailyRoutineGenerator.generate(date)
        assertFalse(routine.isComplete)
        assertFalse(routine.tasks.first().completed)

        val allDone = DailyRoutineGenerator.generate(
            date,
            completedGames = routine.tasks.map { it.gameType }.toSet()
        )
        assertTrue(allDone.isComplete)
        assertEquals(3, allDone.completedCount)
    }
}
