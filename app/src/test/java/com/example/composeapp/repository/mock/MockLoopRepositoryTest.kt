package com.example.composeapp.repository.mock

import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DeliveryStatus
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.local.FakeSentMessageDao
import com.example.composeapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockFreelyRepositoryTest {

    private val repository = MockFreelyRepository(FakeSentMessageDao(), Dispatchers.Unconfined)

    private fun thread(id: String): ChatThread = runBlocking {
        (repository.getChatThread(id).first() as Result.Success).data
    }

    private fun inbox(): Inbox = runBlocking { (repository.getInbox().first() as Result.Success).data }

    private fun send(conversationId: String, text: String) = runBlocking {
        repository.sendMessage(conversationId, text).first()
    }

    @Test
    fun sentMessageIsAppendedToTheThread() {
        val before = thread("maya")
        assertTrue(before.isContactTyping)

        send("maya", "On my way!")

        val after = thread("maya")
        assertEquals(before.messages.size + 1, after.messages.size)
        val sent = after.messages.last() as ChatMessage.Text
        assertEquals("On my way!", sent.text)
        assertTrue(sent.fromMe)
        assertEquals(DeliveryStatus.Sent, sent.deliveryStatus)
        assertFalse(after.isContactTyping)
    }

    @Test
    fun inboxMovesTheLatestSentConversationToTheTop() {
        send("david", "Loved the photo")
        send("family", "Thanks Mom!")

        val conversations = inbox().conversations
        assertEquals(listOf("family", "david"), conversations.take(2).map { it.id })
        val family = conversations.first()
        assertEquals("Thanks Mom!", family.preview)
        assertEquals("You", family.sender)
        assertEquals(0, family.unreadCount)
        assertEquals(MockFreelyData.inbox.conversations.size, conversations.size)
    }

    @Test
    fun firstMessageToAContactStartsAConversation() {
        send("sofia", "Hi Sofia!")

        val conversations = inbox().conversations
        assertEquals(MockFreelyData.inbox.conversations.size + 1, conversations.size)
        assertEquals("Sofia", conversations.first().title)
        assertEquals(1, thread("sofia").messages.size)
    }

    @Test
    fun unknownConversationIsAnError() = runBlocking {
        assertTrue(repository.getChatThread("nobody").first() is Result.Error)
    }
}
