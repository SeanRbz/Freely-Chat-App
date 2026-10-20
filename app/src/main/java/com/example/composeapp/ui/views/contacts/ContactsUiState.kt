package com.example.composeapp.ui.views.contacts

import com.example.composeapp.repository.entities.Person

data class ContactsUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    /** Contacts matching [query]. */
    val contacts: List<Person> = emptyList(),
    val totalCount: Int = 0,
)
