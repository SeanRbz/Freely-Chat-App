package com.example.composeapp.ui.views.messages

import com.example.composeapp.repository.entities.ActiveFreely
import com.example.composeapp.repository.entities.Conversation

data class InboxUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val activeFreelys: List<ActiveFreely> = emptyList(),
    /** Conversations matching [query]. */
    val conversations: List<Conversation> = emptyList(),
    val unreadCount: Int = 0,
    val dailyPing: String = "",
)
