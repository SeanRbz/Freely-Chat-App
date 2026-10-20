package com.example.composeapp.repository.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    /** Emits the saved session, or null when signed out, and again on every change. */
    @Query("SELECT * FROM session WHERE id = ${SessionEntity.SINGLE_ROW_ID}")
    fun observe(): Flow<SessionEntity?>

    @Upsert
    suspend fun save(session: SessionEntity)

    @Query("DELETE FROM session")
    suspend fun clear()
}
