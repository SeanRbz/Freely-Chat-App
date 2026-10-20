package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.usecases.GetSessionUseCase
import com.example.composeapp.repository.usecases.SignOutUseCase
import com.example.composeapp.ui.views.splash.SessionUiState
import com.example.composeapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Watches the saved session. SplashActivity skips sign-in when there is one, and MainActivity
 * goes back to sign-in once Log Out clears it.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<SessionUiState> = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            getSessionUseCase(Unit).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, session = result.data) }
                    }

                    // A session that can't be read counts as signed out.
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, session = null) }
                    }

                    else -> {}
                }
            }
        }
    }

    /** Clears the saved session; [uiState] then reports signed out. */
    fun signOut() {
        viewModelScope.launch {
            signOutUseCase(Unit).collect()
        }
    }
}
