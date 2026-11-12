package com.example.chess.data

import com.example.chess.data.dao.ChessDao
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TacticsProgressEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity
import com.example.chess.data.firebase.FirestoreSyncService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max
import kotlin.math.roundToInt

class ChessRepository(
    private val dao: ChessDao,
    private val firestoreSync: FirestoreSyncService? = null
) {

    val userStats: Flow<UserStatsEntity?> = dao.getUserStats()
    val allSessions: Flow<List<TrainingSessionEntity>> = dao.getAllSessions()
    val allProgress: Flow<List<TacticsProgressEntity>> = dao.getAllPuzzleProgress()
    val savedGames: Flow<List<SavedGameEntity>> = dao.getAllSavedGames()

    suspend fun getOrCreateUserStats(): UserStatsEntity {
        val existing = dao.getUserStats().firstOrNull()
        if (existing != null) {
            return existing
        }
        val defaultStats = UserStatsEntity(
            id = 1,
            currentElo = 1200,
            highestElo = 1200,
            puzzlesSolved = 0,
            puzzlesFailed = 0,
            accuracyPercent = 100f,
            streakDays = 1,
            lastTrainedDate = System.currentTimeMillis()
        )
        dao.insertOrUpdateUserStats(defaultStats)
        return defaultStats
    }

    suspend fun recordPuzzleResult(
        puzzleId: String,
        solvedCorrectly: Boolean,
        attempts: Int,
        timeSeconds: Int,
        puzzleElo: Int,
        displayName: String? = null,
        email: String? = null
    ): Int {
        val progressEntity = TacticsProgressEntity(
            puzzleId = puzzleId,
            solvedCorrectly = solvedCorrectly,
            attempts = attempts,
            timeSeconds = timeSeconds,
            dateCompleted = System.currentTimeMillis()
        )
        dao.insertPuzzleProgress(progressEntity)
        firestoreSync?.syncPuzzleProgress(progressEntity)

        val current = getOrCreateUserStats()
        val kFactor = 24
        val expectedScore = 1.0 / (1.0 + Math.pow(10.0, (puzzleElo - current.currentElo) / 400.0))
        val actualScore = if (solvedCorrectly) 1.0 else 0.0
        val eloDelta = (kFactor * (actualScore - expectedScore)).roundToInt().let {
            if (solvedCorrectly) max(5, it) else minOf(-5, it)
        }

        val newElo = max(600, current.currentElo + eloDelta)
        val newHighest = max(current.highestElo, newElo)
        val newSolved = if (solvedCorrectly) current.puzzlesSolved + 1 else current.puzzlesSolved
        val newFailed = if (!solvedCorrectly) current.puzzlesFailed + 1 else current.puzzlesFailed
        val total = newSolved + newFailed
        val newAccuracy = if (total > 0) (newSolved.toFloat() / total * 100f) else 100f

        val updated = current.copy(
            currentElo = newElo,
            highestElo = newHighest,
            puzzlesSolved = newSolved,
            puzzlesFailed = newFailed,
            accuracyPercent = newAccuracy,
            lastTrainedDate = System.currentTimeMillis()
        )
        dao.insertOrUpdateUserStats(updated)
        firestoreSync?.syncUserProfile(updated, displayName, email)

        return eloDelta
    }

    suspend fun recordTrainingSession(
        sessionType: String,
        category: String,
        solvedCount: Int,
        totalCount: Int,
        eloChange: Int,
        durationSeconds: Int
    ) {
        val accuracy = if (totalCount > 0) ((solvedCount.toFloat() / totalCount) * 100).roundToInt() else 100
        val session = TrainingSessionEntity(
            sessionType = sessionType,
            category = category,
            solvedCount = solvedCount,
            totalCount = totalCount,
            eloChange = eloChange,
            durationSeconds = durationSeconds,
            accuracyPercent = accuracy
        )
        val id = dao.insertSession(session)
        firestoreSync?.syncTrainingSession(session.copy(id = id))
    }

    suspend fun saveGameAnalysis(
        title: String,
        pgnOrMoves: String,
        fen: String,
        evaluation: String,
        notes: String
    ): Long {
        val game = SavedGameEntity(
            title = title,
            pgnOrMoves = pgnOrMoves,
            fen = fen,
            evaluation = evaluation,
            userNotes = notes
        )
        val id = dao.insertSavedGame(game)
        firestoreSync?.syncSavedGame(game.copy(id = id))
        return id
    }

    suspend fun syncAllToCloud(displayName: String?, email: String?) {
        val current = getOrCreateUserStats()
        firestoreSync?.syncUserProfile(current, displayName, email)
    }

    suspend fun deleteSavedGame(id: Long) {
        dao.deleteSavedGame(id)
    }
}
