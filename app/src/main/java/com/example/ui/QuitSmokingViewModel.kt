package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TimeComponents(
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val elapsedMillis: Long = 0
)

data class DashboardStats(
    val moneySaved: Double = 0.0,
    val cigarettesAvoided: Double = 0.0,
    val lifeRegainedMinutes: Double = 0.0
)

class QuitSmokingViewModel(private val repository: QuitSmokingRepository) : ViewModel() {

    val userSettings: StateFlow<UserSettings?> = repository.userSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val cravingLogs: StateFlow<List<CravingLog>> = repository.cravingLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val slipUpLogs: StateFlow<List<SlipUpLog>> = repository.slipUpLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _timeComponents = MutableStateFlow(TimeComponents())
    val timeComponents: StateFlow<TimeComponents> = _timeComponents.asStateFlow()

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    private var tickerJob: Job? = null

    init {
        viewModelScope.launch {
            userSettings.collect { settings ->
                tickerJob?.cancel()
                if (settings != null) {
                    startTicker(settings.quitDateMillis)
                } else {
                    _timeComponents.value = TimeComponents()
                    _stats.value = DashboardStats()
                }
            }
        }
    }

    private fun startTicker(quitDateMillis: Long) {
        tickerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val elapsed = now - quitDateMillis
                val safeElapsed = if (elapsed < 0) 0L else elapsed

                val seconds = (safeElapsed / 1000) % 60
                val minutes = (safeElapsed / (1000 * 60)) % 60
                val hours = (safeElapsed / (1000 * 60 * 60)) % 24
                val days = safeElapsed / (1000 * 60 * 60 * 24)

                _timeComponents.value = TimeComponents(days, hours, minutes, seconds, safeElapsed)

                val settings = userSettings.value
                if (settings != null) {
                    val cigsPerDay = settings.cigarettesPerDay
                    val cigsPerPack = settings.cigarettesPerPack
                    val pricePerPack = settings.pricePerPack

                    val cigRatePerMs = cigsPerDay.toDouble() / (24.0 * 60.0 * 60.0 * 1000.0)
                    val avoided = safeElapsed.toDouble() * cigRatePerMs
                    val pricePerCig = pricePerPack / cigsPerPack.toDouble()
                    val saved = avoided * pricePerCig
                    val lifeMinutes = avoided * 11.0

                    _stats.value = DashboardStats(
                        moneySaved = saved,
                        cigarettesAvoided = avoided,
                        lifeRegainedMinutes = lifeMinutes
                    )
                }

                delay(1000L)
            }
        }
    }

    fun saveSettings(
        quitDateMillis: Long,
        cigarettesPerDay: Int,
        cigarettesPerPack: Int,
        pricePerPack: Double,
        currencySymbol: String = "$"
    ) {
        viewModelScope.launch {
            val settings = UserSettings(
                id = 0,
                quitDateMillis = quitDateMillis,
                cigarettesPerDay = cigarettesPerDay,
                cigarettesPerPack = cigarettesPerPack,
                pricePerPack = pricePerPack,
                currencySymbol = currencySymbol
            )
            repository.saveUserSettings(settings)
        }
    }

    fun logCraving(intensity: String, trigger: String, notes: String) {
        viewModelScope.launch {
            val log = CravingLog(
                intensity = intensity,
                trigger = trigger,
                notes = notes
            )
            repository.insertCravingLog(log)
        }
    }

    fun logSlipUp(cigarettesCount: Int, notes: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val log = SlipUpLog(
                timestamp = now,
                count = cigarettesCount,
                notes = notes
            )
            repository.insertSlipUpLog(log)

            val currentSettings = userSettings.value
            if (currentSettings != null) {
                val updatedSettings = currentSettings.copy(quitDateMillis = now)
                repository.saveUserSettings(updatedSettings)
            }
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}

class QuitSmokingViewModelFactory(private val repository: QuitSmokingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuitSmokingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuitSmokingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
