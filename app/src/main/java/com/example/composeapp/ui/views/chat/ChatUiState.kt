package com.example.composeapp.ui.views.chat

import com.example.composeapp.repository.entities.ChatThread

data class ChatUiState(
    val isLoading: Boolean = true,
    val thread: ChatThread? = null,
    val draft: String = "",
)
