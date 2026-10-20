package com.example.composeapp.ui.views.discover

import com.example.composeapp.repository.entities.Icebreaker
import com.example.composeapp.repository.entities.Person

data class DiscoverUiState(
    val isLoading: Boolean = true,
    val radiusLabel: String = "",
    val circles: List<Person> = emptyList(),
    /** Everyone the featured card can cycle through: the featured person, then the circles. */
    val deck: List<Person> = emptyList(),
    val featured: Person? = null,
    val icebreaker: Icebreaker? = null,
    val wavedIds: Set<String> = emptySet(),
    val savedIds: Set<String> = emptySet(),
) {
    val hasWavedAtFeatured: Boolean get() = featured != null && featured.id in wavedIds
    val isFeaturedSaved: Boolean get() = featured != null && featured.id in savedIds
}
