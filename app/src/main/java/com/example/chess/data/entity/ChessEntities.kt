package com.example.chess.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val currentElo: Int = 1200,
    val highestElo: Int = 1200,
    val puzzlesSolved: Int = 0,
    val puzzlesFailed: Int = 0,
    val accuracyPercent: Float = 100f,
    val streakDays: Int = 1,
    val lastTrainedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionType: String, // "TÁCTICA", "APERTURAS", "ANÁLISIS"
    val category: String, // "Tenedores", "Clavadas", "Ruy López", etc.
    val solvedCount: Int,
    val totalCount: Int,
    val eloChange: Int,
    val durationSeconds: Int,
    val accuracyPercent: Int
)

@Entity(tableName = "tactics_progress")
data class TacticsProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val puzzleId: String,
    val solvedCorrectly: Boolean,
    val attempts: Int,
    val timeSeconds: Int,
    val dateCompleted: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_games")
data class SavedGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pgnOrMoves: String,
    val fen: String,
    val timestamp: Long = System.currentTimeMillis(),
    val evaluation: String,
    val userNotes: String
)
