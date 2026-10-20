package com.example.composeapp.repository.local

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.SessionRepository
import com.example.composeapp.repository.entities.AuthProvider
import com.example.composeapp.repository.entities.Session
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Sign-in is mocked: every attempt succeeds and is remembered in the database until Log Out. */
class LocalSessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SessionRepository {

    override fun observeSession(): Flow<Result<Session?>> = sessionDao.observe()
        .map<SessionEntity?, Result<Session?>> { Result.Success(it?.toSession()) }
        .catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)

    override fun signIn(provider: AuthProvider, identifier: String?): Flow<Result<Session>> = flow<Result<Session>> {
        val session = SessionEntity(provider = provider, identifier = identifier, signedInAt = System.currentTimeMillis())
        sessionDao.save(session)
        emit(Result.Success(session.toSession()))
    }.catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)

    override fun signOut(): Flow<Result<Unit>> = flow<Result<Unit>> {
        sessionDao.clear()
        emit(Result.Success(Unit))
    }.catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)
}
