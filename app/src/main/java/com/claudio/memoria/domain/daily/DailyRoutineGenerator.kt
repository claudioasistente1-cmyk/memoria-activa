package com.claudio.memoria.domain.daily

import com.claudio.memoria.domain.model.DailyRoutine
import com.claudio.memoria.domain.model.DailyTask
import com.claudio.memoria.domain.model.GameType
import java.time.LocalDate
import kotlin.random.Random

object DailyRoutineGenerator {

    const val TASKS_PER_DAY = 3

    fun generate(
        date: LocalDate,
        levels: Map<GameType, Int> = emptyMap(),
        completedGames: Set<GameType> = emptySet()
    ): DailyRoutine {
        val random = Random(date.toEpochDay())
        val chosen = GameType.values().toList().shuffled(random).take(TASKS_PER_DAY)
        val tasks = chosen.map { game ->
            DailyTask(
                gameType = game,
                level = (levels[game] ?: 1).coerceIn(1, 8),
                completed = game in completedGames
            )
        }
        return DailyRoutine(date = date.toString(), tasks = tasks)
    }
}
