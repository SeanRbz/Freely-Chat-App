package com.example.composeapp.repository.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory stand-in for the Room DAO that answers the same queries. */
class FakeSentMessageDao : SentMessageDao {
    private val rows = MutableStateFlow<List<SentMessageEntity>>(emptyList())

    override fun observe(conversationId: String): Flow<List<SentMessageEntity>> =
        rows.map { all -> all.filter { it.conversationId == conversationId } }

    override fun observeLatestPerConversation(): Flow<List<SentMessageEntity>> =
        rows.map { all -> all.groupBy { it.conversationId }.values.map { messages -> messages.maxBy { it.id } } }

    override suspend fun insert(message: SentMessageEntity): Long {
        val id = rows.value.size + 1L
        rows.update { it + message.copy(id = id) }
        return id
    }
}
