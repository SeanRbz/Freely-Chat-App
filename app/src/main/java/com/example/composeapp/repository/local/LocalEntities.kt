package com.example.composeapp.repository.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.composeapp.repository.entities.AuthProvider
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.Session
import com.example.composeapp.utils.DateUtils

// Tables in FreelyDatabase. They stay out of repository/entities, which holds the domain and Gson models.

/** Single-row table: the row exists while someone is signed in. */
@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val provider: AuthProvider,
    val identifier: String?,
    val signedInAt: Long,
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}

/** A message the user sent. Stands in for the server's copy until a backend exists. */
@Entity(tableName = "sent_messages", indices = [Index("conversationId")])
data class SentMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val text: String,
    val sentAt: Long,
)

fun SessionEntity.toSession(): Session =
    Session(provider = provider, identifier = identifier, signedInAt = signedInAt)

fun SentMessageEntity.toChatMessage(): ChatMessage = ChatMessage.Text(
    id = "local-$id",
    fromMe = true,
    time = DateUtils.displayTime(sentAt),
    text = text,
    deliveryStatus = DeliveryStatus.Sent,
)
