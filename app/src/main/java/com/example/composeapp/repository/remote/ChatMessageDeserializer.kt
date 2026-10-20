package com.example.composeapp.repository.remote

import com.example.composeapp.repository.entities.ChatMessage
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/** Gson can't build the sealed ChatMessage on its own; the API's "type" field names the subclass. */
class ChatMessageDeserializer : JsonDeserializer<ChatMessage> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ChatMessage {
        val subclass = when (val type = json.asJsonObject.get("type")?.asString) {
            "Text" -> ChatMessage.Text::class.java
            "VoiceMemo" -> ChatMessage.VoiceMemo::class.java
            "Place" -> ChatMessage.Place::class.java
            else -> throw JsonParseException("Unknown chat message type: $type")
        }
        return context.deserialize(json, subclass)
    }
}
