package com.example.chess.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TacticsProgressEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    private fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    suspend fun syncUserProfile(stats: UserStatsEntity, displayName: String?, email: String?) {
        val uid = getCurrentUserId() ?: return
        try {
            val userDoc = db.collection("users").document(uid)
            val data = hashMapOf<String, Any>(
                "userId" to uid,
                "currentElo" to stats.currentElo,
                "highestElo" to stats.highestElo,
                "puzzlesSolved" to stats.puzzlesSolved,
                "puzzlesFailed" to stats.puzzlesFailed,
                "accuracyPercent" to stats.accuracyPercent,
                "streakDays" to stats.streakDays,
                "displayName" to (displayName ?: ""),
                "email" to (email ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userDoc.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSync", "Perfil de usuario sincronizado en Firestore")
        } catch (e: Exception) {
            Log.w("FirestoreSync", "No se pudo sincronizar perfil: ${e.message}")
        }
    }

    suspend fun syncTrainingSession(session: TrainingSessionEntity) {
        val uid = getCurrentUserId() ?: return
        try {
            val docRef = db.collection("users").document(uid).collection("sessions").document()
            val data = hashMapOf<String, Any>(
                "userId" to uid,
                "sessionType" to session.sessionType,
                "category" to session.category,
                "solvedCount" to session.solvedCount,
                "totalCount" to session.totalCount,
                "eloChange" to session.eloChange,
                "durationSeconds" to session.durationSeconds,
                "accuracyPercent" to session.accuracyPercent,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d("FirestoreSync", "Sesión sincronizada en Firestore: ${session.category}")
        } catch (e: Exception) {
            Log.w("FirestoreSync", "No se pudo sincronizar sesión: ${e.message}")
        }
    }

    suspend fun syncSavedGame(game: SavedGameEntity) {
        val uid = getCurrentUserId() ?: return
        try {
            val docRef = db.collection("users").document(uid).collection("saved_games").document(game.id.toString())
            val data = hashMapOf<String, Any>(
                "userId" to uid,
                "title" to game.title,
                "pgnOrMoves" to game.pgnOrMoves,
                "fen" to game.fen,
                "evaluation" to game.evaluation,
                "userNotes" to game.userNotes,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSync", "Partida guardada sincronizada en Firestore: ${game.title}")
        } catch (e: Exception) {
            Log.w("FirestoreSync", "No se pudo sincronizar partida: ${e.message}")
        }
    }

    suspend fun syncPuzzleProgress(progress: TacticsProgressEntity) {
        val uid = getCurrentUserId() ?: return
        try {
            val docRef = db.collection("users").document(uid).collection("progress").document(progress.puzzleId)
            val data = hashMapOf<String, Any>(
                "userId" to uid,
                "puzzleId" to progress.puzzleId,
                "solvedCorrectly" to progress.solvedCorrectly,
                "attempts" to progress.attempts,
                "timeSeconds" to progress.timeSeconds,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("FirestoreSync", "No se pudo sincronizar progreso: ${e.message}")
        }
    }
}
