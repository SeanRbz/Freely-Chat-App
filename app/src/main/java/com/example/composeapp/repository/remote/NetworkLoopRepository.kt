package com.example.composeapp.repository.remote

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
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Reads the Freely screens from the Freely API. When a call fails (offline, timeout, HTTP error or an
 * unreadable body), it serves the same data built into MockFreelyData instead. The API can't take sent
 * messages yet, so those are saved in Room and merged into the inbox and threads, as MockFreelyRepository does.
 */
class NetworkFreelyRepository @Inject constructor(
    private val service: FreelyService,
    private val sentMessageDao: SentMessageDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FreelyRepository {

    // Only needed when a first message to a contact starts a conversation, so it's fetched on demand.
    // Only API results are cached, so one failed call doesn't pin the built-in copy.
    @Volatile
    private var cachedContacts: List<Person>? = null

    override fun getDiscoverFeed(): Flow<Result<DiscoverFeed>> = load {
        fetch({ service.getDiscoverFeed() }) { MockFreelyData.discoverFeed }
    }

    override fun getInbox(): Flow<Result<Inbox>> =
        flow { emit(fetch({ service.getInbox() }) { MockFreelyData.inbox }) }
            .combine(sentMessageDao.observeLatestPerConversation()) { inbox, latest ->
                val startsConversation = latest.any { message ->
                    inbox.conversations.none { it.id == message.conversationId }
                }
                inbox.withSentMessages(latest, if (startsConversation) contacts() else emptyList())
            }
            .asResult()

    override fun getChatThread(conversationId: String): Flow<Result<ChatThread>> =
        flow { emit(fetch({ service.getChatThread(conversationId) }) { MockFreelyData.chatThread(conversationId) }) }
            .combine(sentMessageDao.observe(conversationId)) { thread, sent -> thread.withSentMessages(sent) }
            .asResult()

    override fun sendMessage(conversationId: String, text: String): Flow<Result<ChatMessage>> = load {
        sentMessageDao.save(conversationId, text)
    }

    override fun getContacts(): Flow<Result<List<Person>>> = load { fetchContacts() }

    override fun getCurrentUser(): Flow<Result<UserProfile>> = load {
        fetch({ service.getCurrentUser() }) { MockFreelyData.currentUser }
    }

    private suspend fun contacts(): List<Person> = cachedContacts ?: fetchContacts()

    private suspend fun fetchContacts(): List<Person> =
        fetch({ service.getContacts().also { cachedContacts = it } }) { MockFreelyData.contacts }

    /**
     * Calls the API, or returns [fallback]'s built-in copy of the same data if the call throws. A null
     * fallback means there's no built-in copy (an unknown chat id), so the API's error stands.
     */
    private suspend fun <T : Any> fetch(call: suspend () -> T, fallback: () -> T?): T =
        try {
            call()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            fallback() ?: throw e
        }

    private fun <T> load(block: suspend () -> T): Flow<Result<T>> = flow { emit(block()) }.asResult()

    private fun <T> Flow<T>.asResult(): Flow<Result<T>> =
        map<T, Result<T>> { Result.Success(it) }
            .catch { emit(Result.Error(it)) }.flowOn(ioDispatcher)
}
