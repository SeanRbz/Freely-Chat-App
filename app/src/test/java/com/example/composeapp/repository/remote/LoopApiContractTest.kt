package com.example.composeapp.repository.remote

import com.example.composeapp.di.module.AppModule
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.UserProfile
import com.example.composeapp.repository.mock.MockFreelyData
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Parses every saved response in the Postman collection with the app's own Gson setup and checks it
 * equals MockFreelyData, so the mock server and the built-in data can't drift apart unnoticed.
 */
class FreelyApiContractTest {

    private val gson = AppModule.provideGson()

    // Unit tests run from the app module directory.
    private val requests = gson.fromJson(File("../postman/freely-mock-api.postman_collection.json").readText(), JsonObject::class.java)
        .getAsJsonArray("item").map { it.asJsonObject }

    /** The saved example bodies of one request, keyed by example name. */
    private fun examples(request: String): Map<String, String> =
        requests.first { it["name"].asString == request }.getAsJsonArray("response")
            .associate { it.asJsonObject["name"].asString to it.asJsonObject["body"].asString }

    private fun body(request: String) = examples(request).getValue("200 OK")

    @Test
    fun discoverFeed() {
        assertEquals(MockFreelyData.discoverFeed, gson.fromJson(body("Discover feed"), DiscoverFeed::class.java))
    }

    @Test
    fun inbox() {
        assertEquals(MockFreelyData.inbox, gson.fromJson(body("Inbox"), Inbox::class.java))
    }

    @Test
    fun everyChatThread() {
        val threads = examples("Chat thread").filterKeys { !it.startsWith("404") }
        threads.forEach { (id, json) ->
            assertEquals(id, MockFreelyData.chatThread(id), gson.fromJson(json, ChatThread::class.java))
        }
        assertEquals(13, threads.size)
    }

    @Test
    fun contacts() {
        val type = object : TypeToken<List<Person>>() {}.type
        assertEquals(MockFreelyData.contacts, gson.fromJson<List<Person>>(body("Contacts"), type))
    }

    @Test
    fun currentUser() {
        assertEquals(MockFreelyData.currentUser, gson.fromJson(body("Current user"), UserProfile::class.java))
    }
}
