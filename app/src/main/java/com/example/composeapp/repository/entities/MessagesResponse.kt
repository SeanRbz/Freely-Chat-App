package com.example.composeapp.repository.entities

import kotlinx.serialization.Serializable

@Serializable
data class MessagesResponse(
    val list: List<MessageData> = listOf(),
    override val statusCode: Int,
    override val message: String
): BaseResponseEntity

@Serializable
data class MessageData(
    val message: String? = null,
    val imageBase64: String? = null,
    val lastSeen: String
)