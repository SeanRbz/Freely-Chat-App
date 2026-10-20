package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.entities.LoginRequest
import com.example.composeapp.repository.entities.LoginResponse
import com.example.composeapp.repository.remote.UserRepositoryImpl
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class LoginUserUseCaseParams(
    val email: String,
    val password: String
)

class LoginUserUseCase @Inject constructor(
    private val repository: UserRepositoryImpl,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
): FlowUseCase<LoginUserUseCaseParams, LoginResponse>(ioDispatcher) {

    override fun execute(parameters: LoginUserUseCaseParams): Flow<Result<LoginResponse>> {
        val body = LoginRequest(email = parameters.email, password = parameters.password)
        return repository.login(body = body)
    }

}