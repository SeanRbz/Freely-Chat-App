package com.example.composeapp.repository.mock

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.FreelyRepository
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.UserProfile
import com.example.composeapp.repository.local.SentMessageDao
import com.example.composeapp.repository.local.save
import com.example.composeapp.repository.local.withSentMessages
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Serves the built-in Stitch mock-up data with the same Flow<Result<T>> shape as NetworkFreelyRepository,
 * for working offline. Sent messages are saved in Room and merged into the threads and the inbox.
 */
class MockFreelyRepository @Inject constructor(
    private val sentMessageDao: SentMessageDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FreelyRepository {

    override fun getDiscoverFeed(): Flow<Result<DiscoverFeed>> = load { MockFreelyData.discoverFeed }

    override fun getInbox(): Flow<Result<Inbox>> = observe(sentMessageDao.observeLatestPerConversation()) { latest ->
        MockFreelyData.inbox.withSentMessages(latest, MockFreelyData.contacts)
    }

    override fun getChatThread(conversationId: String): Flow<Result<ChatThread>> =
        observe(sentMessageDao.observe(conversationId)) { sent ->
            val thread = MockFreelyData.chatThread(conversationId)
                ?: throw NoSuchElementException("No chat thread for '$conversationId'")
            thread.withSentMessages(sent)
        }

    override fun sendMessage(conversationId: String, text: String): Flow<Result<ChatMessage>> = load {
        sentMessageDao.save(conversationId, text)
    }

    override fun getContacts(): Flow<Result<List<Person>>> = load { MockFreelyData.contacts }

    override fun getCurrentUser(): Flow<Result<UserProfile>> = load { MockFreelyData.currentUser }

    private fun <T> load(block: suspend () -> T): Flow<Result<T>> = flow<Result<T>> {
        emit(Result.Success(block()))
    }.catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)

    /** Emits [transform] of each value from [source], e.g. every time the sent-messages table changes. */
    private fun <S, T> observe(source: Flow<S>, transform: (S) -> T): Flow<Result<T>> =
        source.map<S, Result<T>> { Result.Success(transform(it)) }
            .catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)
}
