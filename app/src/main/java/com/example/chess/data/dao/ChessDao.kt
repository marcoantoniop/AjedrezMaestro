package com.example.chess.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TacticsProgressEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChessDao {

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM training_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<TrainingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity): Long

    @Query("SELECT * FROM tactics_progress ORDER BY dateCompleted DESC")
    fun getAllPuzzleProgress(): Flow<List<TacticsProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzleProgress(progress: TacticsProgressEntity): Long

    @Query("SELECT * FROM saved_games ORDER BY timestamp DESC")
    fun getAllSavedGames(): Flow<List<SavedGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedGame(game: SavedGameEntity): Long

    @Query("DELETE FROM saved_games WHERE id = :id")
    suspend fun deleteSavedGame(id: Long)

    @Query("DELETE FROM training_sessions")
    suspend fun clearSessions()
}
