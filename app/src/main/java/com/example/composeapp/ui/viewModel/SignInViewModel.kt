package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.entities.AuthProvider
import com.example.composeapp.repository.usecases.SignInUseCase
import com.example.composeapp.repository.usecases.SignInUseCaseParams
import com.example.composeapp.ui.views.signin.SignInMethod
import com.example.composeapp.ui.views.signin.SignInUiState
import com.example.composeapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the sign-in form. Signing in is mocked: any valid phone or email, or a social button,
 * is saved as the session and continues to the app.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<SignInUiState> = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun setMethod(method: SignInMethod) {
        _uiState.update { it.copy(method = method) }
    }

    fun setPhone(phone: String) {
        // Keep digits and the usual formatting characters only.
        val cleaned = phone.filter { it.isDigit() || it in " ()-" }.take(16)
        _uiState.update { it.copy(phone = cleaned) }
    }

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email.trim()) }
    }

    /** Signs in with the phone number or email from the form. */
    fun continueWithForm() {
        val state = _uiState.value
        if (!state.canContinue) return
        when (state.method) {
            SignInMethod.Phone -> signIn(AuthProvider.Phone, "${state.countryCode} ${state.phone}")
            SignInMethod.Email -> signIn(AuthProvider.Email, state.email)
        }
    }

    /** Signs in with Apple or Google. */
    fun continueWith(provider: AuthProvider) {
        signIn(provider, identifier = null)
    }

    private fun signIn(provider: AuthProvider, identifier: String?) {
        if (_uiState.value.isSigningIn) return
        _uiState.update { it.copy(isSigningIn = true) }
        viewModelScope.launch {
            signInUseCase(SignInUseCaseParams(provider, identifier)).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isSigningIn = false, isSignedIn = true) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isSigningIn = false) }
                    }

                    else -> {}
                }
            }
        }
    }
}
