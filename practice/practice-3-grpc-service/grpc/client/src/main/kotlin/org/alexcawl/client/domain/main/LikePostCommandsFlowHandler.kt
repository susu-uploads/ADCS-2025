package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.interactions.InteractionsClient
import org.alexcawl.contract.interactions.LikePostRequest
import org.alexcawl.contract.interactions.LikePostResponse
import org.alexcawl.kotea.CommandsFlowHandler

internal class LikePostCommandsFlowHandler(
    private val interactionsClient: InteractionsClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<LikePostCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<LikePostCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (
                val response = interactionsClient.likePost(
                    request = LikePostRequest(
                        postId = command.postId,
                        authorId = command.authorId,
                    ),
                )
            ) {
                is LikePostResponse.Success -> emit(OnPostUpdatedEvent(post = response.post))
                is LikePostResponse.ValidationFailure -> emit(OnMainFailureEvent(message = response.message))
                is LikePostResponse.PostNotFound -> emit(OnMainFailureEvent(message = response.message))
                is LikePostResponse.AuthorNotFound -> emit(OnMainFailureEvent(message = response.message))
                is LikePostResponse.AlreadyLiked -> emit(OnMainFailureEvent(message = response.message))
            }
        }
    }
}
