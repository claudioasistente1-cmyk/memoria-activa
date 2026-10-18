package com.claudio.memoria.domain.model

data class GameResult(
    val gameType: GameType,
    val score: Int,
    val correct: Int,
    val total: Int,
    val durationMs: Long,
    val level: Int,
    val timestamp: Long
)

data class DailyTask(
    val gameType: GameType,
    val level: Int,
    val completed: Boolean
)

data class DailyRoutine(
    val date: String,
    val tasks: List<DailyTask>
) {
    val completedCount: Int get() = tasks.count { it.completed }
    val isComplete: Boolean get() = tasks.isNotEmpty() && tasks.all { it.completed }
}

enum class Difficulty(val level: Int, val label: String) {
    EASY(1, "Facil"),
    MEDIUM(3, "Intermedio"),
    HARD(5, "Avanzado");

    companion object {
        fun clamp(level: Int): Int = level.coerceIn(1, 8)
    }
}
