package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.SessionRepository
import com.example.composeapp.repository.entities.AuthProvider
import com.example.composeapp.repository.entities.Session
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SignInUseCaseParams(
    val provider: AuthProvider,
    /** Phone number or email; null for Apple and Google. */
    val identifier: String?
)

class SignInUseCase @Inject constructor(
    private val repository: SessionRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<SignInUseCaseParams, Session>(ioDispatcher) {

    override fun execute(parameters: SignInUseCaseParams): Flow<Result<Session>> {
        return repository.signIn(provider = parameters.provider, identifier = parameters.identifier)
    }
}
