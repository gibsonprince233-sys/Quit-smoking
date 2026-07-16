package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuitSmokingDao {
    @Query("SELECT * FROM user_settings WHERE id = 0")
    fun getUserSettingsFlow(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 0")
    suspend fun getUserSettingsDirect(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)

    @Query("SELECT * FROM craving_logs ORDER BY timestamp DESC")
    fun getAllCravingLogs(): Flow<List<CravingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCravingLog(log: CravingLog)

    @Query("SELECT * FROM slip_up_logs ORDER BY timestamp DESC")
    fun getAllSlipUpLogs(): Flow<List<SlipUpLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlipUpLog(log: SlipUpLog)

    @Query("DELETE FROM user_settings")
    suspend fun clearUserSettings()

    @Query("DELETE FROM craving_logs")
    suspend fun clearCravingLogs()

    @Query("DELETE FROM slip_up_logs")
    suspend fun clearSlipUpLogs()
}
