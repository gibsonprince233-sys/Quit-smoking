package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 0,
    val quitDateMillis: Long = System.currentTimeMillis(),
    val cigarettesPerDay: Int = 15,
    val cigarettesPerPack: Int = 20,
    val pricePerPack: Double = 12.50,
    val currencySymbol: String = "$"
)

@Entity(tableName = "craving_logs")
data class CravingLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val intensity: String, // "Low", "Medium", "Severe"
    val trigger: String = "Stress", // "Stress", "Social", "Habit", "Boredom", etc.
    val notes: String = ""
)

@Entity(tableName = "slip_up_logs")
data class SlipUpLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val count: Int = 1,
    val notes: String = ""
)
