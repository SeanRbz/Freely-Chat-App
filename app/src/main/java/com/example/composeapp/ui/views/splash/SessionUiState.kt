package com.example.composeapp.ui.views.splash

import com.example.composeapp.repository.entities.Session

data class SessionUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
) {
    val isSignedIn: Boolean
        get() = session != null
}
