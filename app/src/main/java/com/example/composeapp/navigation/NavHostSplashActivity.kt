package com.example.composeapp.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.ui.views.signin.SignInPage
import kotlinx.serialization.Serializable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetSplashNavController(paddingValues: PaddingValues, onSignedIn: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .fillMaxSize(),
        navController = navController,
        startDestination = SplashScreens.SignIn.route
    ) {
        composable(SplashScreens.SignIn.route) {
            SignInPage(viewModel = hiltViewModel(), onSignedIn = onSignedIn)
        }
    }
}

@Serializable
sealed class SplashScreens(val route: String) {
    object SignIn : SplashScreens("SignInPage")
}
