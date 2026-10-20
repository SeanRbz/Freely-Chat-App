package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.entities.LoginResponse
import com.example.composeapp.repository.entities.SignupRequest
import com.example.composeapp.repository.remote.UserRepositoryImpl
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SignupUserUseCaseParams(
    val email: String,
    val password: String
)

class SignupUserUseCase @Inject constructor(
    private val repository: UserRepositoryImpl,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
): FlowUseCase<SignupUserUseCaseParams, LoginResponse>(ioDispatcher) {

    override fun execute(parameters: SignupUserUseCaseParams): Flow<Result<LoginResponse>> {
        val body = SignupRequest(email = parameters.email, password = parameters.password)
        return repository.signup(body = body)
    }

}