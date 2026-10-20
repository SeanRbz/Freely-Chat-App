package com.example.composeapp.repository.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val userData: UsersData,
    override val statusCode: Int,
    override val message: String
) : BaseResponseEntity