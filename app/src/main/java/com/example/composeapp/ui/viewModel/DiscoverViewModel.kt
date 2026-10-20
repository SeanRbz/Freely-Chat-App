package com.example.composeapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.usecases.GetDiscoverFeedUseCase
import com.example.composeapp.ui.views.discover.DiscoverUiState
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
class DiscoverViewModel @Inject constructor(
    private val getDiscoverFeedUseCase: GetDiscoverFeedUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<DiscoverUiState> = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getDiscoverFeedUseCase(Unit).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val feed = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                radiusLabel = feed.radiusLabel,
                                circles = feed.circles,
                                deck = listOf(feed.featured) + feed.circles,
                                featured = feed.featured,
                                icebreaker = feed.icebreaker,
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

    fun selectPerson(person: Person) {
        _uiState.update { it.copy(featured = person) }
    }

    /** Skips the featured person and shows the next one in the deck. */
    fun pass() {
        _uiState.update { state ->
            if (state.deck.isEmpty()) return@update state
            val next = (state.deck.indexOf(state.featured) + 1) % state.deck.size
            state.copy(featured = state.deck[next])
        }
    }

    fun wave() {
        _uiState.update { state ->
            val person = state.featured ?: return@update state
            state.copy(wavedIds = state.wavedIds + person.id)
        }
    }

    fun toggleSaved() {
        _uiState.update { state ->
            val person = state.featured ?: return@update state
            val saved = if (person.id in state.savedIds) state.savedIds - person.id else state.savedIds + person.id
            state.copy(savedIds = saved)
        }
    }
}
