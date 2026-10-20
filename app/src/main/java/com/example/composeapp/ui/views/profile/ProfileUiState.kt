package com.example.composeapp.ui.views.profile

import com.example.composeapp.repository.entities.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
)
