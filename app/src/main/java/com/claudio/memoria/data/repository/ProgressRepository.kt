package com.claudio.memoria.data.repository

import com.claudio.memoria.data.db.DailyCompletionEntity
import com.claudio.memoria.data.db.GameResultEntity
import com.claudio.memoria.data.db.GameStat
import com.claudio.memoria.data.db.ProgressDao
import com.claudio.memoria.domain.daily.DailyRoutineGenerator
import com.claudio.memoria.domain.model.DailyRoutine
import com.claudio.memoria.domain.model.GameType
import com.claudio.memoria.util.TimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

data class DayHistory(
    val date: LocalDate,
    val completedCount: Int,
    val totalTasks: Int
)

class ProgressRepository(private val dao: ProgressDao) {

    fun observeDailyRoutine(date: LocalDate = TimeUtils.today()): Flow<DailyRoutine> {
        val dayKey = date.toString()
        return combine(
            dao.observeRecentResults(120),
            dao.observeCompletions(dayKey)
        ) { results, completions ->
            val completed = completions.mapNotNull { GameType.fromId(it.gameId) }.toSet()
            DailyRoutineGenerator.generate(
                date = date,
                levels = suggestLevels(results),
                completedGames = completed
            )
        }
    }

    fun observeTodayRoutine(): Flow<DailyRoutine> = observeDailyRoutine(TimeUtils.today())

    fun observeStats(): Flow<List<GameStat>> = dao.observeStats()

    fun observeRecentResults(): Flow<List<GameResultEntity>> = dao.observeRecentResults(60)

    fun observeTotalGamesPlayed(): Flow<Int> = dao.observeTotalGamesPlayed()

    fun observeActiveDays(): Flow<Int> = dao.observeActiveDaysCount()

    fun observeHistory(): Flow<List<DayHistory>> = dao.observeAllCompletions().map { rows ->
        rows.groupBy { it.dayKey }
            .mapNotNull { (key, values) ->
                val date = runCatching { LocalDate.parse(key) }.getOrNull()
                    ?: return@mapNotNull null
                val completedIds = values.map { it.gameId }.toSet()
                val routine = DailyRoutineGenerator.generate(date)
                DayHistory(
                    date = date,
                    completedCount = routine.tasks.count { it.gameType.id in completedIds },
                    totalTasks = routine.tasks.size
                )
            }
            .sortedByDescending { it.date }
            .take(30)
    }

    suspend fun recordGame(
        gameType: GameType,
        correct: Int,
        total: Int,
        durationMs: Long,
        level: Int
    ) {
        val now = System.currentTimeMillis()
        val dayKey = TimeUtils.todayKey()
        val score = (correct.coerceAtLeast(0) * 10) + (if (total > 0 && correct == total) 20 else 0)
        dao.insertResult(
            GameResultEntity(
                gameId = gameType.id,
                score = score,
                correct = correct,
                total = total,
                durationMs = durationMs,
                level = level,
                timestamp = now,
                dayKey = dayKey
            )
        )
        dao.insertCompletion(
            DailyCompletionEntity(
                dayKey = dayKey,
                gameId = gameType.id,
                completedAt = now,
                score = score,
                level = level
            )
        )
    }

    suspend fun nextLevel(gameType: GameType): Int {
        val recent = dao.recentResultsForGame(gameType.id, 3)
        return nextLevelFrom(recent.map { it.level to it.accuracy() })
    }

    /** Suggests the next level per game from previous results. Pure and testable. */
    fun suggestLevels(results: List<GameResultEntity>): Map<GameType, Int> {
        return results.groupBy { it.gameId }
            .mapNotNull { (gameId, rows) ->
                val type = GameType.fromId(gameId) ?: return@mapNotNull null
                val latest = rows.maxByOrNull { it.timestamp } ?: return@mapNotNull null
                type to adaptLevel(latest.level, latest.accuracy())
            }
            .toMap()
    }

    companion object {
        fun adaptLevel(currentLevel: Int, accuracy: Double): Int = when {
            accuracy >= 0.75 -> (currentLevel + 1).coerceAtMost(8)
            accuracy < 0.45 -> (currentLevel - 1).coerceAtLeast(1)
            else -> currentLevel.coerceIn(1, 8)
        }

        private fun nextLevelFrom(recent: List<Pair<Int, Double>>): Int {
            if (recent.isEmpty()) return 1
            val level = recent.first().first
            val avgAccuracy = recent.map { it.second }.average()
            return adaptLevel(level, avgAccuracy)
        }
    }
}

fun GameResultEntity.accuracy(): Double = if (total <= 0) 0.0 else correct.toDouble() / total
