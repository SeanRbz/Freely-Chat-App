package com.example.composeapp.repository.entities

import androidx.annotation.Keep
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@Keep
@OptIn(ExperimentalSerializationApi::class)
interface BaseResponseEntity {
    @JsonNames("status_code")
    val statusCode: Int
    val message: String
}