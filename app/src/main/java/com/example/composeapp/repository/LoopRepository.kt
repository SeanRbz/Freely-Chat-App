package com.example.composeapp.repository

import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.UserProfile
import com.example.composeapp.utils.Result
import kotlinx.coroutines.flow.Flow

/** Data source for the Freely screens; AppModule binds NetworkFreelyRepository. */
interface FreelyRepository {
    fun getDiscoverFeed(): Flow<Result<DiscoverFeed>>

    /** Emits again whenever a message is sent, so the previews stay current. */
    fun getInbox(): Flow<Result<Inbox>>

    /** Emits again whenever a message is sent to this conversation. */
    fun getChatThread(conversationId: String): Flow<Result<ChatThread>>

    fun sendMessage(conversationId: String, text: String): Flow<Result<ChatMessage>>

    fun getContacts(): Flow<Result<List<Person>>>

    fun getCurrentUser(): Flow<Result<UserProfile>>
}
