package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.FreelyRepository
import com.example.composeapp.repository.entities.ChatThread
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatThreadUseCase @Inject constructor(
    private val repository: FreelyRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<String, ChatThread>(ioDispatcher) {

    override fun execute(parameters: String): Flow<Result<ChatThread>> {
        return repository.getChatThread(conversationId = parameters)
    }
}
