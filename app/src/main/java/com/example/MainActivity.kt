package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.QuitSmokingDatabase
import com.example.data.QuitSmokingRepository
import com.example.ui.QuitSmokingDashboard
import com.example.ui.QuitSmokingViewModel
import com.example.ui.QuitSmokingViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob SDK
        MobileAds.initialize(this) {}
        
        // 1. Initialize the Room database, Dao, and Repository
        val database = QuitSmokingDatabase.getDatabase(applicationContext)
        val dao = database.quitSmokingDao()
        val repository = QuitSmokingRepository(dao)
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 2. Instantiate the ViewModel using our Factory
                    val viewModel: QuitSmokingViewModel = viewModel(
                        factory = QuitSmokingViewModelFactory(repository)
                    )
                    
                    // 3. Render the Quit Smoking single-screen Dashboard
                    QuitSmokingDashboard(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
