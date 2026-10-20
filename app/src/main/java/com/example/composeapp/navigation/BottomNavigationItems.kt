package com.example.composeapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItems(
    val title: String,
    val badgeCount: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val nav: MainActivityScreens
)
