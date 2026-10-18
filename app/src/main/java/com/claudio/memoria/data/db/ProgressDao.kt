package com.claudio.memoria.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Insert
    suspend fun insertResult(result: GameResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: DailyCompletionEntity)

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentResults(limit: Int = 100): Flow<List<GameResultEntity>>

    @Query("SELECT * FROM game_results WHERE gameId = :gameId ORDER BY timestamp DESC LIMIT :limit")
    fun observeResultsForGame(gameId: String, limit: Int = 30): Flow<List<GameResultEntity>>

    @Query(
        "SELECT gameId, COUNT(*) AS timesPlayed, MAX(score) AS bestScore, MAX(level) AS bestLevel " +
            "FROM game_results GROUP BY gameId"
    )
    fun observeStats(): Flow<List<GameStat>>

    @Query("SELECT * FROM daily_completions WHERE dayKey = :dayKey")
    fun observeCompletions(dayKey: String): Flow<List<DailyCompletionEntity>>

    @Query("SELECT * FROM daily_completions WHERE dayKey = :dayKey")
    suspend fun getCompletions(dayKey: String): List<DailyCompletionEntity>

    @Query("SELECT * FROM daily_completions ORDER BY dayKey DESC")
    fun observeAllCompletions(): Flow<List<DailyCompletionEntity>>

    @Query("SELECT * FROM game_results WHERE gameId = :gameId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recentResultsForGame(gameId: String, limit: Int): List<GameResultEntity>

    @Query("SELECT COUNT(*) FROM game_results")
    fun observeTotalGamesPlayed(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT dayKey) FROM daily_completions")
    fun observeActiveDaysCount(): Flow<Int>
}
