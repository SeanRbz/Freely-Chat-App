package com.example.composeapp.repository.local

import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.Conversation
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.PreviewKind
import com.example.composeapp.utils.DateUtils

// The Freely API can't take sent messages yet, so they live in Room. Both FreelyRepository
// implementations merge them into what they read, the way a server would return them.

/** Saves a sent message and returns it as the thread shows it. */
suspend fun SentMessageDao.save(conversationId: String, text: String): ChatMessage {
    val message = SentMessageEntity(conversationId = conversationId, text = text, sentAt = System.currentTimeMillis())
    return message.copy(id = insert(message)).toChatMessage()
}

/**
 * Moves conversations with a sent message to the top, newest first, previewing that message.
 * A first message to someone in [contacts] starts a new conversation with them.
 */
fun Inbox.withSentMessages(latest: List<SentMessageEntity>, contacts: List<Person>): Inbox {
    if (latest.isEmpty()) return this
    val updated = latest.sortedByDescending { it.id }.mapNotNull { message ->
        val conversation = conversations.firstOrNull { it.id == message.conversationId }
            ?: contacts.firstOrNull { it.id == message.conversationId }?.toConversation()
            ?: return@mapNotNull null
        conversation.copy(
            sender = "You",
            preview = message.text,
            previewKind = PreviewKind.Text,
            timeLabel = DateUtils.inboxTimeLabel(message.sentAt),
            unreadCount = 0,
            deliveryStatus = DeliveryStatus.Sent,
        )
    }
    val updatedIds = updated.map { it.id }.toSet()
    return copy(conversations = updated + conversations.filter { it.id !in updatedIds })
}

/** Appends sent messages after the thread's history; the contact stops "typing" once you reply. */
fun ChatThread.withSentMessages(sent: List<SentMessageEntity>): ChatThread =
    if (sent.isEmpty()) this
    else copy(messages = messages + sent.map { it.toChatMessage() }, isContactTyping = false)

private fun Person.toConversation() = Conversation(
    id = id,
    title = name,
    avatarUrls = listOf(avatarUrl),
    presence = presence,
    hasNewMoment = hasNewMoment,
    preview = "",
    timeLabel = "",
)
