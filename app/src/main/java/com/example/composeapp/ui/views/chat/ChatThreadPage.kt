package com.example.composeapp.ui.views.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.Presence
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
import com.example.composeapp.ui.theme.WarmSand
import com.example.composeapp.ui.theme.ZestyMint
import com.example.composeapp.ui.viewModel.ChatThreadViewModel
import com.example.composeapp.ui.views.base.Avatar
import com.example.composeapp.ui.views.base.EmptyHint
import com.example.composeapp.ui.views.base.IconCircle
import com.example.composeapp.ui.views.base.FreelyCard
import com.example.composeapp.ui.views.base.FreelyLogoMark
import com.example.composeapp.ui.views.base.FreelyTextInput
import com.example.composeapp.ui.views.base.SoftPill

private val IncomingBubbleShape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 6.dp)
private val OutgoingBubbleShape = RoundedCornerShape(24.dp, 24.dp, 6.dp, 24.dp)

@Composable
fun ChatThreadPage(
    viewModel: ChatThreadViewModel,
    selfAvatarUrl: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChatThreadContent(
        uiState = uiState,
        selfAvatarUrl = selfAvatarUrl,
        onBack = onBack,
        onDraftChange = viewModel::onDraftChange,
        onSend = viewModel::sendMessage,
        modifier = modifier
    )
}

/** Stateless "Active Chat Thread" screen from the Stitch design. */
@Composable
fun ChatThreadContent(
    uiState: ChatUiState,
    selfAvatarUrl: String?,
    onBack: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        ChatTopBar(selfAvatarUrl = selfAvatarUrl, onBack = onBack)
        val thread = uiState.thread
        if (thread == null) {
            if (!uiState.isLoading) EmptyHint("This conversation isn't available.")
        } else {
            ThreadMessages(thread = thread, modifier = Modifier.weight(1f))
            ChatInputBar(
                draft = uiState.draft,
                placeholder = "Message ${thread.contactName.substringBefore(' ')}...",
                onDraftChange = onDraftChange,
                onSend = onSend
            )
        }
    }
}

@Composable
private fun ChatTopBar(selfAvatarUrl: String?, onBack: () -> Unit) {
    Column(Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = DeepEspresso)
            }
            FreelyLogoMark(size = 36.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                "Chat Thread",
                style = MaterialTheme.typography.titleLarge,
                color = DeepEspresso,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "More options",
                tint = DeepEspresso,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if (selfAvatarUrl != null) {
                Avatar(
                    url = selfAvatarUrl,
                    size = 40.dp,
                    ring = SolidColor(Blush),
                    ringWidth = 2.dp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun ThreadMessages(thread: ChatThread, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    // Scroll to the newest message when one is sent, but leave the initial position alone.
    val messageCount = thread.messages.size
    val initialCount = remember { mutableIntStateOf(messageCount) }
    LaunchedEffect(messageCount) {
        if (messageCount > initialCount.intValue) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "contact") { ContactCard(thread) }
        item(key = "day") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SoftPill(
                    thread.dayLabel,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = CocoaMute
                )
            }
        }
        if (thread.messages.isEmpty()) {
            item(key = "empty") { EmptyHint("Say hello to ${thread.contactName.substringBefore(' ')} 👋") }
        }
        items(thread.messages, key = { it.id }) { message ->
            when (message) {
                is ChatMessage.Place -> SharedPlaceCard(message)
                else -> if (message.fromMe) OutgoingMessage(message) else IncomingMessage(message, thread.contactAvatarUrl)
            }
        }
        if (thread.isContactTyping) {
            item(key = "typing") {
                TypingIndicator(name = thread.contactName.substringBefore(' '), avatarUrl = thread.contactAvatarUrl)
            }
        }
    }
}

@Composable
private fun ContactCard(thread: ChatThread) {
    FreelyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                url = thread.contactAvatarUrl,
                size = 52.dp,
                ring = SolidColor(Blush),
                ringWidth = 2.dp,
                presence = thread.presence,
                contentDescription = thread.contactName
            )
            Column(Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        thread.contactName,
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepEspresso,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (thread.isFavorite) {
                        Icon(Icons.Outlined.FavoriteBorder, "Favorite", tint = SunnyCoral, modifier = Modifier.size(20.dp))
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isOnline = thread.presence == Presence.Online
                    if (isOnline) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(ZestyMint, CircleShape)
                        )
                    }
                    Text(
                        thread.statusLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isOnline) MaterialTheme.colorScheme.tertiary else CocoaMute,
                        maxLines = 1
                    )
                }
            }
            IconCircle(Icons.Outlined.Call, PeachCreme, SunnyCoral, size = 40.dp, iconSize = 20.dp, contentDescription = "Call")
            IconCircle(
                Icons.Outlined.Videocam, WarmSand, MaterialTheme.colorScheme.secondary,
                size = 40.dp, iconSize = 20.dp, contentDescription = "Video call"
            )
            IconCircle(
                Icons.Outlined.Info, MaterialTheme.colorScheme.surfaceContainer, DeepEspresso,
                size = 40.dp, iconSize = 20.dp, contentDescription = "Contact info"
            )
        }
    }
}

@Composable
private fun IncomingMessage(message: ChatMessage, avatarUrl: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Avatar(url = avatarUrl, size = 32.dp, modifier = Modifier.padding(bottom = 22.dp))
        Column {
            when (message) {
                is ChatMessage.Text -> Text(
                    message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepEspresso,
                    modifier = Modifier
                        .clip(IncomingBubbleShape)
                        .background(Color.White)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                )

                is ChatMessage.VoiceMemo -> VoiceMemoBubble(message)
                is ChatMessage.Place -> Unit
            }
            Text(
                message.time,
                style = MaterialTheme.typography.bodySmall,
                color = CocoaMute,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun OutgoingMessage(message: ChatMessage) {
    val text = (message as? ChatMessage.Text) ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp),
        horizontalAlignment = Alignment.End
    ) {
        Box {
            Text(
                text.text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier
                    .clip(OutgoingBubbleShape)
                    .background(FreelyGradients.OutgoingBubble)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            )
            text.reaction?.let { reaction ->
                SoftPill(
                    reaction,
                    containerColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = 16.dp)
                        .shadow(2.dp, CircleShape)
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = if (text.reaction != null) 20.dp else 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(message.time, style = MaterialTheme.typography.bodySmall, color = CocoaMute)
            if (text.deliveryStatus == DeliveryStatus.Read) {
                Icon(Icons.Outlined.DoneAll, contentDescription = "Read", tint = SunnyCoral, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// Bar heights (dp) for the decorative voice-memo waveform.
private val WaveformBars = listOf(10, 18, 8, 22, 14, 26, 12, 20, 16, 24, 10, 18, 8, 14, 6)

@Composable
private fun VoiceMemoBubble(message: ChatMessage.VoiceMemo) {
    Row(
        modifier = Modifier
            .clip(IncomingBubbleShape)
            .background(Color.White)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCircle(
            icon = Icons.Filled.PlayArrow,
            containerColor = GoldenMarigold,
            contentColor = DeepEspresso,
            size = 48.dp,
            iconSize = 26.dp,
            contentDescription = "Play voice memo"
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.height(28.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WaveformBars.forEachIndexed { index, height ->
                    val color = when {
                        index < 5 -> SunnyCoral
                        index < 10 -> GoldenMarigold
                        else -> Blush
                    }
                    Box(
                        Modifier
                            .width(3.dp)
                            .height(height.dp)
                            .background(color, CircleShape)
                    )
                }
            }
            Row(modifier = Modifier.width(160.dp)) {
                Text(message.duration, style = MaterialTheme.typography.labelLarge, color = DeepEspresso)
                Spacer(Modifier.weight(1f))
                Text("Voice memo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun TypingIndicator(name: String, avatarUrl: String?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(url = avatarUrl, size = 28.dp)
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(PeachCreme)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$name is typing", style = MaterialTheme.typography.labelLarge, color = DeepEspresso)
            TypingDots()
        }
    }
}

@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "typing")
    listOf(SunnyCoral, GoldenMarigold, ZestyMint).forEachIndexed { index, color ->
        val alpha by transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 500, delayMillis = index * 150), RepeatMode.Reverse),
            label = "dot$index"
        )
        Box(
            Modifier
                .size(7.dp)
                // Read the animated value in the draw phase so the dots don't recompose every frame.
                .graphicsLayer { this.alpha = alpha }
                .background(color, CircleShape)
        )
    }
}

@Composable
private fun SharedPlaceCard(place: ChatMessage.Place) {
    FreelyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCircle(Icons.Outlined.LocalCafe, LemonFizz, MaterialTheme.colorScheme.secondary, size = 52.dp, iconSize = 26.dp)
            Column(Modifier.weight(1f)) {
                Text(place.name, style = MaterialTheme.typography.titleMedium, color = DeepEspresso)
                Text(place.details, style = MaterialTheme.typography.bodySmall, color = CocoaMute)
            }
            SoftPill(
                "Directions",
                containerColor = PeachCreme,
                contentColor = MaterialTheme.colorScheme.primary,
                borderColor = Blush
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    draft: String,
    placeholder: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val canSend = draft.isNotBlank()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Attach", tint = DeepEspresso)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.Mood, contentDescription = "Emoji", tint = DeepEspresso)
        }
        FreelyTextInput(
            value = draft,
            onValueChange = onDraftChange,
            placeholder = placeholder,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        if (!canSend) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Mic, contentDescription = "Record voice note", tint = DeepEspresso)
            }
        } else {
            Spacer(Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(FreelyGradients.Ember)
                .clickable(enabled = canSend, onClickLabel = "Send message", role = Role.Button, onClick = onSend),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun ChatThreadPagePreview() {
    FreelyTheme {
        ChatThreadContent(
            uiState = ChatUiState(isLoading = false, thread = MockFreelyData.chatThread("maya")),
            selfAvatarUrl = MockFreelyData.currentUser.avatarUrl,
            onBack = {},
            onDraftChange = {},
            onSend = {}
        )
    }
}
