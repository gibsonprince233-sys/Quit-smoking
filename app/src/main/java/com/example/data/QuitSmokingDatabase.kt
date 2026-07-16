package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserSettings::class, CravingLog::class, SlipUpLog::class], version = 1, exportSchema = false)
abstract class QuitSmokingDatabase : RoomDatabase() {
    abstract fun quitSmokingDao(): QuitSmokingDao

    companion object {
        @Volatile
        private var INSTANCE: QuitSmokingDatabase? = null

        fun getDatabase(context: Context): QuitSmokingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuitSmokingDatabase::class.java,
                    "quit_smoking_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
