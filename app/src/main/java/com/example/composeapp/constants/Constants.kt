package com.example.composeapp.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import com.example.composeapp.navigation.BottomNavigationItems
import com.example.composeapp.navigation.MainActivityScreens


object Constants {

    // Discover is the first tab and the landing screen after sign-in.
    val NavBottomList = listOf(
        BottomNavigationItems(
            title = "Discover",
            badgeCount = 0,
            icon = Icons.Outlined.Explore,
            selectedIcon = Icons.Filled.Explore,
            nav = MainActivityScreens.Discover
        ),
        BottomNavigationItems(
            title = "Messages",
            badgeCount = 0,
            icon = Icons.Outlined.ChatBubbleOutline,
            selectedIcon = Icons.Filled.ChatBubble,
            nav = MainActivityScreens.Messages
        ),
        BottomNavigationItems(
            title = "Contacts",
            badgeCount = 0,
            icon = Icons.Outlined.Group,
            selectedIcon = Icons.Filled.Group,
            nav = MainActivityScreens.Contacts
        ),
        BottomNavigationItems(
            title = "Profile",
            badgeCount = 0,
            icon = Icons.Outlined.Person,
            selectedIcon = Icons.Filled.Person,
            nav = MainActivityScreens.Profile
        )
    )
}
