package com.claudio.memoria.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "game_results", indices = [Index("gameId"), Index("timestamp")])
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: String,
    val score: Int,
    val correct: Int,
    val total: Int,
    val durationMs: Long,
    val level: Int,
    val timestamp: Long,
    val dayKey: String
)

@Entity(
    tableName = "daily_completions",
    primaryKeys = ["dayKey", "gameId"],
    indices = [Index("dayKey")]
)
data class DailyCompletionEntity(
    val dayKey: String,
    val gameId: String,
    val completedAt: Long,
    val score: Int,
    val level: Int
)

data class GameStat(
    val gameId: String,
    val timesPlayed: Int,
    val bestScore: Int,
    val bestLevel: Int
)
