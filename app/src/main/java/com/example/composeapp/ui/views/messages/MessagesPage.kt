package com.example.composeapp.ui.views.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeapp.repository.entities.ActiveFreely
import com.example.composeapp.repository.entities.Conversation
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.PreviewKind
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.ui.theme.Blush
import com.example.composeapp.ui.theme.CocoaMute
import com.example.composeapp.ui.theme.DeepEspresso
import com.example.composeapp.ui.theme.GoldenMarigold
import com.example.composeapp.ui.theme.LemonFizz
import com.example.composeapp.ui.theme.FreelyGradients
import com.example.composeapp.ui.theme.FreelyTheme
import com.example.composeapp.ui.theme.PeachCreme
import com.example.composeapp.ui.theme.SunnyCoral
import com.example.composeapp.ui.theme.ZestyMint
import com.example.composeapp.ui.viewModel.InboxViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.EmptyHint
import com.example.composeapp.ui.views.base.IconCircle
import com.example.composeapp.ui.views.base.FreelyCard
import com.example.composeapp.ui.views.base.FreelySearchField
import com.example.composeapp.ui.views.base.SectionHeader
import com.example.composeapp.ui.views.base.SoftPill
import com.example.composeapp.ui.views.base.UnreadBadge

@Composable
fun MessagesPage(
    viewModel: InboxViewModel,
    onOpenConversation: (Conversation) -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MessagesContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onOpenConversation = onOpenConversation,
        onCompose = onCompose,
        modifier = modifier
    )
}

/** Stateless "Messages & Inbox" screen from the Stitch design. */
@Composable
fun MessagesContent(
    uiState: InboxUiState,
    onQueryChange: (String) -> Unit,
    onOpenConversation: (Conversation) -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // Bottom padding keeps the last card clear of the compose button.
            contentPadding = PaddingValues(top = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "search") {
                FreelySearchField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    placeholder = "Search messages & friends",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    trailing = { Icon(Icons.Outlined.Mic, contentDescription = "Voice search", tint = CocoaMute) }
                )
            }
            item(key = "loops-header") {
                SectionHeader(
                    title = "Active Messages",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    afterTitle = {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(ZestyMint, CircleShape)
                        )
                    },
                    trailing = {
                        Text("View All", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                )
            }
            item(key = "loops") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.activeFreelys, key = { it.id }) { loop -> ActiveFreelyItem(loop) }
                }
            }
            item(key = "conversations-header") {
                SectionHeader(
                    title = "Conversations",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    trailing = {
                        if (uiState.unreadCount > 0) {
                            SoftPill(
                                "${uiState.unreadCount} Unread",
                                containerColor = PeachCreme,
                                contentColor = MaterialTheme.colorScheme.primary,
                                borderColor = Blush,
                                leading = {
                                    Icon(Icons.Outlined.MarkChatUnread, null, tint = SunnyCoral, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                    }
                )
            }
            if (!uiState.isLoading && uiState.conversations.isEmpty()) {
                item(key = "empty") { EmptyHint("No conversations match \"${uiState.query}\"") }
            }
            items(uiState.conversations, key = { it.id }) { conversation ->
                ConversationCard(
                    conversation = conversation,
                    onClick = { onOpenConversation(conversation) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            if (uiState.dailyPing.isNotEmpty()) {
                item(key = "daily-ping") {
                    DailyPingCard(uiState.dailyPing, Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp))
                }
            }
        }
        ComposeButton(
            onClick = onCompose,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}

@Composable
private fun ActiveFreelyItem(loop: ActiveFreely) {
    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box {
            Avatar(
                url = loop.avatarUrl,
                size = 64.dp,
                ring = if (loop.hasNewMoment) FreelyGradients.Ring else SolidColor(MaterialTheme.colorScheme.surfaceVariant),
                presence = if (loop.isSelf) null else loop.presence,
                contentDescription = loop.name
            )
            if (loop.isSelf) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .background(SunnyCoral, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add your ping", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
        Text(
            loop.name,
            style = MaterialTheme.typography.labelLarge,
            color = if (loop.hasNewMoment) DeepEspresso else CocoaMute,
            fontWeight = if (loop.hasNewMoment) FontWeight.ExtraBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ConversationCard(conversation: Conversation, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val hasUnread = conversation.unreadCount > 0
    FreelyCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConversationAvatar(conversation)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        listOfNotNull(conversation.title, conversation.titleEmoji).joinToString(" "),
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepEspresso,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        conversation.timeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (hasUnread) SunnyCoral else CocoaMute
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (conversation.previewKind) {
                        PreviewKind.Photo -> Icon(Icons.Outlined.PhotoCamera, null, tint = GoldenMarigold, modifier = Modifier.size(18.dp))
                        PreviewKind.VoiceNote -> Icon(Icons.Outlined.GraphicEq, null, tint = SunnyCoral, modifier = Modifier.size(18.dp))
                        PreviewKind.Text -> Unit
                    }
                    Text(
                        buildAnnotatedString {
                            conversation.sender?.let {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = DeepEspresso)) { append("$it: ") }
                            }
                            append(conversation.preview)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasUnread) DeepEspresso else CocoaMute,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    conversation.deliveryStatus?.let { status ->
                        Icon(
                            if (status == DeliveryStatus.Read) Icons.Outlined.DoneAll else Icons.Outlined.Done,
                            contentDescription = if (status == DeliveryStatus.Read) "Read" else "Sent",
                            tint = if (status == DeliveryStatus.Read) MaterialTheme.colorScheme.tertiaryContainer else CocoaMute.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (hasUnread) UnreadBadge(conversation.unreadCount)
                }
            }
        }
    }
}

@Composable
private fun ConversationAvatar(conversation: Conversation) {
    val urls = conversation.avatarUrls
    if (urls.size >= 2) {
        // Group: two stacked faces.
        Box(Modifier.size(56.dp)) {
            Avatar(url = urls[0], size = 38.dp, modifier = Modifier.align(Alignment.TopStart))
            Avatar(
                url = urls[1],
                size = 38.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    } else {
        Avatar(
            url = urls.firstOrNull(),
            size = 56.dp,
            ring = if (conversation.hasNewMoment) FreelyGradients.Ring else null,
            ringWidth = 2.dp,
            presence = conversation.presence,
            contentDescription = conversation.title
        )
    }
}

@Composable
private fun DailyPingCard(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(FreelyGradients.DailyPing)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("✨ DAILY PING", style = MaterialTheme.typography.labelMedium, color = LemonFizz)
            Text(text, style = MaterialTheme.typography.titleLarge, color = Color.White)
        }
        IconCircle(
            icon = Icons.AutoMirrored.Outlined.Send,
            containerColor = Color.White.copy(alpha = 0.25f),
            contentColor = Color.White,
            size = 52.dp,
            contentDescription = "Share a ping"
        )
    }
}

@Composable
private fun ComposeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(12.dp, CircleShape, spotColor = SunnyCoral)
            .clip(CircleShape)
            .background(FreelyGradients.Ember)
            .clickable(onClickLabel = "Start a new chat", role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.Edit, contentDescription = "New message", tint = Color.White)
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
fun MessagesPagePreview() {
    val inbox = MockFreelyData.inbox
    FreelyTheme {
        MessagesContent(
            uiState = InboxUiState(
                isLoading = false,
                activeFreelys = inbox.activeFreelys,
                conversations = inbox.conversations,
                unreadCount = inbox.conversations.sumOf { it.unreadCount },
                dailyPing = inbox.dailyPing,
            ),
            onQueryChange = {},
            onOpenConversation = {},
            onCompose = {}
        )
    }
}
