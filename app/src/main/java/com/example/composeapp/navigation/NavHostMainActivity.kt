package com.example.composeapp.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.composeapp.ui.viewModel.ChatThreadViewModel
import com.example.composeapp.ui.views.chat.ChatThreadPage
import com.example.composeapp.ui.views.contacts.ContactsPage
import com.example.composeapp.ui.views.discover.DiscoverPage
import com.example.composeapp.ui.views.messages.MessagesPage
import com.example.composeapp.ui.views.profile.ProfilePage
import kotlinx.serialization.Serializable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetNavigationController(
    innerPadding: PaddingValues,
    navController: NavHostController,
    selfAvatarUrl: String?,
    onLogout: () -> Unit,
) {
    val openChat: (String) -> Unit = { id -> navController.navigate(MainActivityScreens.ChatThread.createRoute(id)) }
    NavHost(
        modifier = Modifier
            .padding(innerPadding)
            // Lets imePadding() inside screens subtract the bars the Scaffold already padded for.
            .consumeWindowInsets(innerPadding)
            .fillMaxSize(),
        navController = navController,
        startDestination = MainActivityScreens.Discover.route
    ) {
        // hiltViewModel() inside each destination scopes the ViewModel to that back stack entry.
        composable(MainActivityScreens.Discover.route) {
            DiscoverPage(viewModel = hiltViewModel(), onSayHello = { person -> openChat(person.id) })
        }
        composable(MainActivityScreens.Messages.route) {
            MessagesPage(
                viewModel = hiltViewModel(),
                onOpenConversation = { conversation -> openChat(conversation.id) },
                onCompose = { navController.navigateToTab(MainActivityScreens.Contacts) }
            )
        }
        composable(MainActivityScreens.Contacts.route) {
            ContactsPage(viewModel = hiltViewModel(), onOpenChat = { person -> openChat(person.id) })
        }
        composable(MainActivityScreens.Profile.route) {
            ProfilePage(viewModel = hiltViewModel(), onLogout = onLogout)
        }
        composable(
            route = MainActivityScreens.ChatThread.route,
            arguments = listOf(navArgument(ChatThreadViewModel.ARG_CONVERSATION_ID) { type = NavType.StringType })
        ) {
            ChatThreadPage(
                viewModel = hiltViewModel(),
                selfAvatarUrl = selfAvatarUrl,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/** Switches bottom-bar tabs, keeping each tab's back stack and scroll state. */
fun NavHostController.navigateToTab(screen: MainActivityScreens) {
    navigate(screen.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Serializable
sealed class MainActivityScreens(val route: String) {
    object Discover : MainActivityScreens("DiscoverPage")
    object Messages : MainActivityScreens("MessagesPage")
    object Contacts : MainActivityScreens("ContactsPage")
    object Profile : MainActivityScreens("ProfilePage")
    object ChatThread : MainActivityScreens("ChatThreadPage/{${ChatThreadViewModel.ARG_CONVERSATION_ID}}") {
        fun createRoute(conversationId: String) = "ChatThreadPage/$conversationId"
    }
}
