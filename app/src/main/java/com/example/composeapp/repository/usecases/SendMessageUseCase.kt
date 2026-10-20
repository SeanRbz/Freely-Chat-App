package com.example.composeapp.repository.usecases

import com.example.composeapp.di.qualifiers.IoDispatcher
import com.example.composeapp.repository.FreelyRepository
import com.example.composeapp.repository.entities.ChatMessage
import com.example.composeapp.utils.FlowUseCase
import com.example.composeapp.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SendMessageUseCaseParams(
    val conversationId: String,
    val text: String
)

class SendMessageUseCase @Inject constructor(
    private val repository: FreelyRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<SendMessageUseCaseParams, ChatMessage>(ioDispatcher) {

    override fun execute(parameters: SendMessageUseCaseParams): Flow<Result<ChatMessage>> {
        return repository.sendMessage(conversationId = parameters.conversationId, text = parameters.text)
    }
}
