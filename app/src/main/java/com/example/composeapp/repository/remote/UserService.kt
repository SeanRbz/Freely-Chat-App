package com.example.composeapp.repository.remote

import com.example.composeapp.repository.entities.BaseResponseEntity
import com.example.composeapp.repository.entities.LoginRequest
import com.example.composeapp.repository.entities.LoginResponse
import com.example.composeapp.repository.entities.SignupRequest
import com.example.composeapp.repository.entities.UserDataResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {

    @Headers("Content-Type: application/json")
    @GET("/userList")
    suspend fun getNearbyUsers(): UserDataResponse

    // Retrofit rejects @Body on GET requests, so these must be POST.
    @Headers("Content-Type: application/json")
    @POST("/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("/signup")
    suspend fun signup(@Body body: SignupRequest): LoginResponse
}