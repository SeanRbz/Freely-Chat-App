package com.example.composeapp.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.constants.Constants
import com.example.composeapp.navigation.MainActivityScreens
import com.example.composeapp.navigation.SetNavigationController
import com.example.composeapp.navigation.navigateToTab
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.viewModel.ProfileViewModel
import com.example.composeapp.ui.viewModel.SessionViewModel
import com.example.composeapp.ui.views.base.FreelyTopBar
import com.example.composeapp.ui.views.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreelyTheme {
                MainActivityView(onLogout = sessionViewModel::signOut)
            }
        }
        lifecycleScope.launch {
            // Back to sign-in once there's no saved session: after Log Out, or if none was ever saved.
            sessionViewModel.uiState.first { !it.isLoading && !it.isSignedIn }
            openSignIn()
        }
    }

    private fun openSignIn() {
        startActivity(
            Intent(this, SplashActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}

@Composable
fun MainActivityView(onLogout: () -> Unit, profileViewModel: ProfileViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    // Observe the back stack as state so the bars recompose when the destination changes.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val selfAvatarUrl = profileState.profile?.avatarUrl
    // Tabs get the shared top and bottom bars; the chat thread is full screen.
    val showTabChrome = currentRoute == null || Constants.NavBottomList.any { it.nav.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTabChrome) {
                FreelyTopBar(
                    avatarUrl = selfAvatarUrl,
                    onSearchClick = { navController.navigateToTab(MainActivityScreens.Contacts) },
                    onAvatarClick = { navController.navigateToTab(MainActivityScreens.Profile) }
                )
            }
        },
        bottomBar = {
            if (showTabChrome) {
                FreelyBottomBar(currentRoute = currentRoute, onSelect = navController::navigateToTab)
            }
        }
    ) { innerPadding ->
        SetNavigationController(
            innerPadding = innerPadding,
            navController = navController,
            selfAvatarUrl = selfAvatarUrl,
            onLogout = onLogout
        )
    }
}

@Composable
private fun FreelyBottomBar(currentRoute: String?, onSelect: (MainActivityScreens) -> Unit) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            Constants.NavBottomList.forEach { item ->
                val selected = currentRoute == item.nav.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(item.nav) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount > 0) {
                                    Badge { Text(text = item.badgeCount.toString()) }
                                }
                            }
                        ) {
                            Icon(if (selected) item.selectedIcon else item.icon, contentDescription = null)
                        }
                    },
                    label = { Text(item.title, style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SunnyCoral,
                        selectedTextColor = SunnyCoral,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = CocoaMute,
                        unselectedTextColor = CocoaMute
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FreelyTheme {
        FreelyBottomBar(currentRoute = MainActivityScreens.Discover.route, onSelect = {})
    }
}
