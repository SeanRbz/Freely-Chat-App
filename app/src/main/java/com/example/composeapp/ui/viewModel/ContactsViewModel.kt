package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.usecases.GetContactsUseCase
import com.example.composeapp.ui.views.contacts.ContactsUiState
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
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<ContactsUiState> = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private var allContacts: List<Person> = emptyList()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            getContactsUseCase(Unit).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        allContacts = result.data
                        _uiState.update {
                            it.copy(isLoading = false, contacts = filter(it.query), totalCount = allContacts.size)
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
        _uiState.update { it.copy(query = query, contacts = filter(query)) }
    }

    private fun filter(query: String): List<Person> {
        if (query.isBlank()) return allContacts
        return allContacts.filter {
            it.name.contains(query, ignoreCase = true) || it.headline.contains(query, ignoreCase = true)
        }
    }
}
