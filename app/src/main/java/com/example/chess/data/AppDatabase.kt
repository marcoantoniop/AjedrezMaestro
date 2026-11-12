package com.example.chess.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chess.data.dao.ChessDao
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TacticsProgressEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity

@Database(
    entities = [
        UserStatsEntity::class,
        TrainingSessionEntity::class,
        TacticsProgressEntity::class,
        SavedGameEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chessDao(): ChessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ajedrez_maestro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
