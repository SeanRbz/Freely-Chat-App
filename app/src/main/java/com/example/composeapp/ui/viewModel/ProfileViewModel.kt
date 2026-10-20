package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.usecases.GetCurrentUserUseCase
import com.example.composeapp.ui.views.profile.ProfileUiState
import com.example.composeapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getCurrentUserUseCase(Unit).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, profile = result.data) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    else -> {}
                }
            }
        }
    }
}
