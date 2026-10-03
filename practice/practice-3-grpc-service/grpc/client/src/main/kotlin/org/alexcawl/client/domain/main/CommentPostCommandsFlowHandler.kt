package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.interactions.CommentPostRequest
import org.alexcawl.contract.interactions.CommentPostResponse
import org.alexcawl.contract.interactions.InteractionsClient
import org.alexcawl.kotea.CommandsFlowHandler

internal class CommentPostCommandsFlowHandler(
    private val interactionsClient: InteractionsClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<CommentPostCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<CommentPostCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (
                val response = interactionsClient.commentPost(
                    request = CommentPostRequest(
                        postId = command.postId,
                        authorId = command.authorId,
                        text = command.text,
                    ),
                )
            ) {
                is CommentPostResponse.Success -> emit(OnPostUpdatedEvent(post = response.post))
                is CommentPostResponse.ValidationFailure -> emit(OnMainFailureEvent(message = response.message))
                is CommentPostResponse.PostNotFound -> emit(OnMainFailureEvent(message = response.message))
                is CommentPostResponse.AuthorNotFound -> emit(OnMainFailureEvent(message = response.message))
            }
        }
    }
}
