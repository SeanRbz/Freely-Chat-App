package com.example.composeapp.ui.views.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.composeapp.navigation.SetSplashNavController
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.viewModel.SessionViewModel
import com.example.composeapp.ui.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()

    // Holds the system splash until we know whether to show sign-in or go straight to the app.
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate() so the splash theme hands over to the app theme.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplash }
        enableEdgeToEdge()
        lifecycleScope.launch {
            if (sessionViewModel.uiState.first { !it.isLoading }.isSignedIn) {
                // Returning user: skip sign-in. The splash stays up until MainActivity covers it.
                openMain()
            } else {
                setContent {
                    FreelyTheme {
                        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
                            SetSplashNavController(padding, onSignedIn = ::openMain)
                        }
                    }
                }
                keepSplash = false
            }
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        // Drop the sign-in flow so Back from the main screen exits instead of returning here.
        finish()
    }
}
