package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.entities.UserDataResponse
import com.example.composeapp.repository.remote.UserRepositoryImpl
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetNearbyUsersUseCase @Inject constructor(
    private val repository: UserRepositoryImpl,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
): FlowUseCase<Unit, UserDataResponse>(ioDispatcher) {

    override fun execute(parameters: Unit): Flow<Result<UserDataResponse>> {
        return repository.getNearbyUsers()
    }

}