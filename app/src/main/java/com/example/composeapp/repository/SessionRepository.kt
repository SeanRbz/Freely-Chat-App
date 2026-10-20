package com.example.composeapp.repository

import com.example.composeapp.repository.entities.AuthProvider
import com.example.composeapp.repository.entities.Session
import com.example.composeapp.utils.Result
import kotlinx.coroutines.flow.Flow

/** The signed-in account; bound to LocalSessionRepository (Room) in AppModule. */
interface SessionRepository {
    /** Emits the current session, or null when signed out, and again whenever it changes. */
    fun observeSession(): Flow<Result<Session?>>

    fun signIn(provider: AuthProvider, identifier: String?): Flow<Result<Session>>

    fun signOut(): Flow<Result<Unit>>
}
