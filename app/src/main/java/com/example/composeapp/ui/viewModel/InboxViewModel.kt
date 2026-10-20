package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.entities.Conversation
import com.example.composeapp.repository.usecases.GetInboxUseCase
import com.example.composeapp.ui.views.messages.InboxUiState
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
class InboxViewModel @Inject constructor(
    private val getInboxUseCase: GetInboxUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<InboxUiState> = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    private var allConversations: List<Conversation> = emptyList()

    init {
        loadInbox()
    }

    private fun loadInbox() {
        viewModelScope.launch {
            getInboxUseCase(Unit).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val inbox = result.data
                        allConversations = inbox.conversations
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                activeFreelys = inbox.activeFreelys,
                                conversations = filter(it.query),
                                unreadCount = inbox.conversations.sumOf { c -> c.unreadCount },
                                dailyPing = inbox.dailyPing,
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    else -> {}
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, conversations = filter(query)) }
    }

    private fun filter(query: String): List<Conversation> {
        if (query.isBlank()) return allConversations
        return allConversations.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.preview.contains(query, ignoreCase = true) ||
                it.sender?.contains(query, ignoreCase = true) == true
        }
    }
}
