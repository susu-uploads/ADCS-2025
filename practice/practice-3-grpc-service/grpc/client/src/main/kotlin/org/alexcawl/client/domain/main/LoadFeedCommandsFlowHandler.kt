package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.posts.ListFeedResponse
import org.alexcawl.contract.posts.PostsClient
import org.alexcawl.kotea.CommandsFlowHandler

internal class LoadFeedCommandsFlowHandler(
    private val postsClient: PostsClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<LoadFeedCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<LoadFeedCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (val response = postsClient.listFeed()) {
                is ListFeedResponse.Success -> emit(OnFeedLoadedEvent(posts = response.posts))
                is ListFeedResponse.ValidationFailure -> emit(OnMainFailureEvent(message = response.message))
            }
        }
    }
}
