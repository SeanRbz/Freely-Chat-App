package com.example.composeapp.repository.entities

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Keep
@Serializable
data class UsersData(
    val name: String? = null,
    val description: String? = null,
    val location: String? = null,
    val imageUrl: String? = null
)

@Keep
@Serializable
data class UserDataResponse(
    val list : List<UsersData>,
    override val statusCode: Int,
    override val message: String
): BaseResponseEntity