package com.example.composeapp.repository.remote

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.entities.LoginRequest
import com.example.composeapp.repository.entities.LoginResponse
import com.example.composeapp.repository.entities.SignupRequest
import com.example.composeapp.repository.entities.UserDataResponse
import kotlinx.coroutines.CoroutineDispatcher
import com.example.composeapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface UsersRepository {
    fun getNearbyUsers(): Flow<Result<UserDataResponse>>

    fun login(body: LoginRequest): Flow<Result<LoginResponse>>

    fun signup(body: SignupRequest): Flow<Result<LoginResponse>>
}
class UserRepositoryImpl @Inject constructor(
    val api: UserService,
    @IoDispatcher private val dispatchersIO: CoroutineDispatcher
):UsersRepository {

    override fun getNearbyUsers(): Flow<Result<UserDataResponse>> = flow<Result<UserDataResponse>> {
        val response = api.getNearbyUsers()
        emit(Result.Success(response))
    }.catch { emit(Result.Error(it)) }.flowOn(dispatchersIO)

    override fun login(body: LoginRequest): Flow<Result<LoginResponse>> = flow<Result<LoginResponse>> {
        val response = api.login(body = body)
        emit(Result.Success(response))
    }.catch { emit(Result.Error(it)) }.flowOn(dispatchersIO)

    override fun signup(body: SignupRequest): Flow<Result<LoginResponse>> = flow<Result<LoginResponse>> {
        val response = api.signup(body = body)
        emit(Result.Success(response))
    }.catch { emit(Result.Error(it)) }.flowOn(dispatchersIO)

}