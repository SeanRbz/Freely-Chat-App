package com.example.composeapp.repository.remote

import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.local.FakeSentMessageDao
import com.example.composeapp.repository.local.save
import com.example.composeapp.repository.mock.MockFreelyData
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Each test follows Arrange-Act-Assert: set up the API and the sent messages, make one repository call,
 * then check one behavior.
 */
class NetworkFreelyRepositoryTest {

    /** Answers like the Postman mock server (the same data as MockFreelyData), or throws [failure]. */
    private class FakeFreelyService : FreelyService {
        var failure: Exception? = null
        var contactsCalls = 0

        override suspend fun getDiscoverFeed() = answer { MockFreelyData.discoverFeed }

        override suspend fun getInbox(): Inbox = answer { MockFreelyData.inbox }

        // An id the server doesn't know fails, like the mock server's 404.
        override suspend fun getChatThread(conversationId: String) =
            answer { checkNotNull(MockFreelyData.chatThread(conversationId)) }

        override suspend fun getContacts(): List<Person> {
            contactsCalls++
            return answer { MockFreelyData.contacts }
        }

        override suspend fun getCurrentUser() = answer { MockFreelyData.currentUser }

        private fun <T> answer(data: () -> T): T = failure?.let { throw it } ?: data()
    }

    private val service = FakeFreelyService()
    private val sentMessages = FakeSentMessageDao()
    private val repository = NetworkFreelyRepository(service, sentMessages, Dispatchers.Unconfined)

    private val offline = IOException("offline")

    private fun <T> Flow<Result<T>>.firstResult(): Result<T> = runBlocking { first() }

    private fun <T> Flow<Result<T>>.firstData(): T = (firstResult() as Result.Success).data

    private fun sent(conversationId: String, text: String) = runBlocking { sentMessages.save(conversationId, text) }

    // The API answers

    @Test
    fun getInbox_messageSentToExistingConversation_movesItToTheTopWithAPreview() {
        // Arrange
        sent("david", "Loved the photo")

        // Act
        val top = repository.getInbox().firstData().conversations.first()

        // Assert
        assertEquals("david", top.id)
        assertEquals("You", top.sender)
        assertEquals("Loved the photo", top.preview)
    }

    @Test
    fun getInbox_messageSentToExistingConversation_doesNotLookUpContacts() {
        // Arrange
        sent("david", "Loved the photo")

        // Act
        repository.getInbox().firstResult()

        // Assert
        assertEquals(0, service.contactsCalls)
    }

    @Test
    fun getInbox_firstMessageToAContact_addsTheirConversation() {
        // Arrange
        sent("sofia", "Hi Sofia!")

        // Act
        val top = repository.getInbox().firstData().conversations.first()

        // Assert
        assertEquals("Sofia", top.title)
    }

    @Test
    fun getInbox_loadedAgain_reusesTheContactsFromTheApi() {
        // Arrange
        sent("sofia", "Hi Sofia!")
        repository.getInbox().firstResult()

        // Act
        repository.getInbox().firstResult()

        // Assert
        assertEquals(1, service.contactsCalls)
    }

    @Test
    fun getChatThread_messageSent_appendsItToTheApiThread() {
        // Arrange
        sent("maya", "On my way!")

        // Act
        val thread = repository.getChatThread("maya").firstData()

        // Assert
        assertEquals(MockFreelyData.chatThread("maya")!!.messages.size + 1, thread.messages.size)
    }

    // The API fails

    @Test
    fun getDiscoverFeed_apiUnreachable_returnsTheBuiltInFeed() {
        // Arrange
        service.failure = offline

        // Act
        val result = repository.getDiscoverFeed().firstResult()

        // Assert
        assertEquals(Result.Success(MockFreelyData.discoverFeed), result)
    }

    @Test
    fun getInbox_apiUnreachable_returnsTheBuiltInInbox() {
        // Arrange
        service.failure = offline

        // Act
        val result = repository.getInbox().firstResult()

        // Assert
        assertEquals(Result.Success(MockFreelyData.inbox), result)
    }

    @Test
    fun getChatThread_apiUnreachable_returnsTheBuiltInThread() {
        // Arrange
        service.failure = offline

        // Act
        val result = repository.getChatThread("maya").firstResult()

        // Assert
        assertEquals(Result.Success(MockFreelyData.chatThread("maya")), result)
    }

    @Test
    fun getContacts_apiUnreachable_returnsTheBuiltInContacts() {
        // Arrange
        service.failure = offline

        // Act
        val result = repository.getContacts().firstResult()

        // Assert
        assertEquals(Result.Success(MockFreelyData.contacts), result)
    }

    @Test
    fun getCurrentUser_serverError_returnsTheBuiltInProfile() {
        // Arrange
        service.failure = HttpException(Response.error<Any>(500, "".toResponseBody()))

        // Act
        val result = repository.getCurrentUser().firstResult()

        // Assert
        assertEquals(Result.Success(MockFreelyData.currentUser), result)
    }

    @Test
    fun getInbox_apiUnreachable_stillShowsSentMessages() {
        // Arrange
        service.failure = offline
        sent("sofia", "Hi Sofia!")

        // Act
        val top = repository.getInbox().firstData().conversations.first()

        // Assert
        assertEquals("Sofia", top.title)
        assertEquals("Hi Sofia!", top.preview)
    }

    @Test
    fun getInbox_apiBackAfterAnOutage_looksUpContactsAgain() {
        // Arrange
        service.failure = offline
        sent("sofia", "Hi Sofia!")
        repository.getInbox().firstResult()
        service.failure = null

        // Act
        repository.getInbox().firstResult()

        // Assert
        assertEquals(2, service.contactsCalls)
    }

    @Test
    fun getChatThread_unknownIdTheApiRejects_returnsAnError() {
        // Arrange
        val unknownId = "nobody"

        // Act
        val result = repository.getChatThread(unknownId).firstResult()

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun getDiscoverFeed_callCancelled_returnsAnErrorInsteadOfTheBuiltInFeed() {
        // Arrange
        service.failure = CancellationException("cancelled")

        // Act
        val result = repository.getDiscoverFeed().firstResult()

        // Assert
        assertTrue(result is Result.Error)
    }
}
