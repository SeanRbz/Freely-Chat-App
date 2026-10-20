package com.example.composeapp.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SentMessageDao {
    @Query("SELECT * FROM sent_messages WHERE conversationId = :conversationId ORDER BY id")
    fun observe(conversationId: String): Flow<List<SentMessageEntity>>

    /** The newest sent message in each conversation, for inbox previews. */
    @Query("SELECT * FROM sent_messages WHERE id IN (SELECT MAX(id) FROM sent_messages GROUP BY conversationId)")
    fun observeLatestPerConversation(): Flow<List<SentMessageEntity>>

    /** Returns the new row id. */
    @Insert
    suspend fun insert(message: SentMessageEntity): Long
}
