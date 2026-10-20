package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.FreelyRepository
import com.example.composeapp.repository.entities.DiscoverFeed
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiscoverFeedUseCase @Inject constructor(
    private val repository: FreelyRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, DiscoverFeed>(ioDispatcher) {

    override fun execute(parameters: Unit): Flow<Result<DiscoverFeed>> {
        return repository.getDiscoverFeed()
    }
}
