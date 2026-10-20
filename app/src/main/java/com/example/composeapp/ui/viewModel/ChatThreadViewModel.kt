package com.example.composeapp.ui.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.usecases.GetChatThreadUseCase
import com.example.composeapp.repository.usecases.SendMessageUseCase
import com.example.composeapp.repository.usecases.SendMessageUseCaseParams
import com.example.composeapp.ui.views.chat.ChatUiState
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
class ChatThreadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChatThreadUseCase: GetChatThreadUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Filled from the navigation argument by hiltViewModel().
    private val conversationId: String = checkNotNull(savedStateHandle[ARG_CONVERSATION_ID])

    init {
        loadThread()
    }

    private fun loadThread() {
        viewModelScope.launch {
            getChatThreadUseCase(conversationId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, thread = result.data) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    else -> {}
                }
            }
        }
    }

    fun onDraftChange(draft: String) {
        _uiState.update { it.copy(draft = draft) }
    }

    /** Saves the draft to the local database; the thread flow from loadThread() then shows it. */
    fun sendMessage() {
        val state = _uiState.value
        val text = state.draft.trim()
        if (state.thread == null || text.isEmpty()) return
        _uiState.update { it.copy(draft = "") }
        viewModelScope.launch {
            sendMessageUseCase(SendMessageUseCaseParams(conversationId, text)).collectLatest { result ->
                if (result is Result.Error) {
                    // Give the text back rather than lose it, unless a new draft was started.
                    _uiState.update { if (it.draft.isEmpty()) it.copy(draft = text) else it }
                }
            }
        }
    }

    companion object {
        const val ARG_CONVERSATION_ID = "conversationId"
    }
}
