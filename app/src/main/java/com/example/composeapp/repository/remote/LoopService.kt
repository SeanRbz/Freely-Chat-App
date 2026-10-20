package com.example.composeapp.repository.remote

import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.repository.entities.Inbox
import com.example.composeapp.repository.entities.Person
import com.example.composeapp.repository.entities.UserProfile
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * The Freely API, served for now by the Postman mock server built from
 * postman/freely-mock-api.postman_collection.json (base URL: BaseUrlService.baseUrl).
 */
interface FreelyService {
    @GET("discover")
    suspend fun getDiscoverFeed(): DiscoverFeed

    @GET("inbox")
    suspend fun getInbox(): Inbox

    /** Takes a conversation id or a person id; unknown ids return 404. */
    @GET("chats/{conversationId}")
    suspend fun getChatThread(@Path("conversationId") conversationId: String): ChatThread

    @GET("contacts")
    suspend fun getContacts(): List<Person>

    @GET("me")
    suspend fun getCurrentUser(): UserProfile
}
