package com.example.composeapp.ui.views.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.DeepEspresso
import com.example.composeapp.ui.theme.FreelyGradients
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.PeachCreme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.viewModel.ContactsViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.EmptyHint
import com.example.composeapp.ui.views.base.IconCircle
import com.example.composeapp.ui.views.base.FreelyCard
import com.example.composeapp.ui.views.base.FreelySearchField
import com.example.composeapp.ui.views.base.SectionHeader
import com.example.composeapp.ui.views.base.SoftPill

@Composable
fun ContactsPage(
    viewModel: ContactsViewModel,
    onOpenChat: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ContactsContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onOpenChat = onOpenChat,
        modifier = modifier
    )
}

/**
 * Friends list. The Stitch project has no Contacts screen, so this reuses the Messages
 * building blocks (search pill, white cards, presence avatars).
 */
@Composable
fun ContactsContent(
    uiState: ContactsUiState,
    onQueryChange: (String) -> Unit,
    onOpenChat: (Person) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "search") {
            FreelySearchField(value = uiState.query, onValueChange = onQueryChange, placeholder = "Search friends")
        }
        item(key = "header") {
            SectionHeader(
                title = "Friends",
                trailing = {
                    if (uiState.totalCount > 0) {
                        SoftPill("${uiState.totalCount} people", containerColor = PeachCreme, contentColor = SunnyCoral)
                    }
                }
            )
        }
        if (!uiState.isLoading && uiState.contacts.isEmpty()) {
            item(key = "empty") { EmptyHint("No friends match \"${uiState.query}\"") }
        }
        items(uiState.contacts, key = { it.id }) { person ->
            ContactRow(person = person, onOpenChat = { onOpenChat(person) })
        }
    }
}

@Composable
private fun ContactRow(person: Person, onOpenChat: () -> Unit) {
    FreelyCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenChat) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                url = person.avatarUrl,
                size = 52.dp,
                ring = if (person.hasNewMoment) FreelyGradients.Ring else SolidColor(MaterialTheme.colorScheme.surfaceVariant),
                ringWidth = 2.dp,
                presence = person.presence,
                contentDescription = person.name
            )
            Column(Modifier.weight(1f)) {
                Text(person.name, style = MaterialTheme.typography.titleMedium, color = DeepEspresso)
                Text(
                    person.headline,
                    style = MaterialTheme.typography.bodySmall,
                    color = CocoaMute,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconCircle(
                icon = Icons.Outlined.ChatBubbleOutline,
                containerColor = PeachCreme,
                contentColor = SunnyCoral,
                size = 44.dp,
                iconSize = 22.dp,
                contentDescription = "Message ${person.firstName}"
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun ContactsPagePreview() {
    FreelyTheme {
        ContactsContent(
            uiState = ContactsUiState(
                isLoading = false,
                contacts = MockFreelyData.contacts,
                totalCount = MockFreelyData.contacts.size
            ),
            onQueryChange = {},
            onOpenChat = {}
        )
    }
}
