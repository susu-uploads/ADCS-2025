package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.posts.CreatePostRequest
import org.alexcawl.contract.posts.CreatePostResponse
import org.alexcawl.contract.posts.PostsClient
import org.alexcawl.kotea.CommandsFlowHandler

internal class CreatePostCommandsFlowHandler(
    private val postsClient: PostsClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<CreatePostCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<CreatePostCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (
                val response = postsClient.createPost(
                    request = CreatePostRequest(
                        authorId = command.authorId,
                        text = command.text,
                    ),
                )
            ) {
                is CreatePostResponse.Success -> emit(OnPostCreatedEvent(post = response.post))
                is CreatePostResponse.ValidationFailure -> emit(OnMainFailureEvent(message = response.message))
                is CreatePostResponse.NotFoundFailure -> emit(OnMainFailureEvent(message = response.message))
            }
        }
    }
}
