package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.interactions.DislikePostRequest
import org.alexcawl.contract.interactions.DislikePostResponse
import org.alexcawl.contract.interactions.InteractionsClient
import org.alexcawl.kotea.CommandsFlowHandler

internal class DislikePostCommandsFlowHandler(
    private val interactionsClient: InteractionsClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<DislikePostCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<DislikePostCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (
                val response = interactionsClient.dislikePost(
                    request = DislikePostRequest(
                        postId = command.postId,
                        authorId = command.authorId,
                    ),
                )
            ) {
                is DislikePostResponse.Success -> emit(OnPostUpdatedEvent(post = response.post))
                is DislikePostResponse.ValidationFailure -> emit(OnMainFailureEvent(message = response.message))
                is DislikePostResponse.PostNotFound -> emit(OnMainFailureEvent(message = response.message))
                is DislikePostResponse.AuthorNotFound -> emit(OnMainFailureEvent(message = response.message))
                is DislikePostResponse.NotLiked -> emit(OnMainFailureEvent(message = response.message))
            }
        }
    }
}
