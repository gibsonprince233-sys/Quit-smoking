package com.example.data

import kotlinx.coroutines.flow.Flow

class QuitSmokingRepository(private val dao: QuitSmokingDao) {
    val userSettings: Flow<UserSettings?> = dao.getUserSettingsFlow()
    val cravingLogs: Flow<List<CravingLog>> = dao.getAllCravingLogs()
    val slipUpLogs: Flow<List<SlipUpLog>> = dao.getAllSlipUpLogs()

    suspend fun saveUserSettings(settings: UserSettings) {
        dao.saveUserSettings(settings)
    }

    suspend fun insertCravingLog(log: CravingLog) {
        dao.insertCravingLog(log)
    }

    suspend fun insertSlipUpLog(log: SlipUpLog) {
        dao.insertSlipUpLog(log)
    }

    suspend fun getUserSettingsDirect(): UserSettings? {
        return dao.getUserSettingsDirect()
    }

    suspend fun resetAllData() {
        dao.clearUserSettings()
        dao.clearCravingLogs()
        dao.clearSlipUpLogs()
    }
}
