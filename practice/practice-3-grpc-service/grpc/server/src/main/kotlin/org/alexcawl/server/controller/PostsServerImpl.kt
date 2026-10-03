package org.alexcawl.server.controller

import org.alexcawl.contract.posts.CreatePostRequest
import org.alexcawl.contract.posts.CreatePostResponse
import org.alexcawl.contract.posts.ListFeedRequest
import org.alexcawl.contract.posts.ListFeedResponse
import org.alexcawl.contract.posts.PostsServer
import org.alexcawl.server.domain.usecase.CreatePostUseCase
import org.alexcawl.server.domain.usecase.CreatePostUseCase.Result
import org.alexcawl.server.domain.usecase.ListFeedUseCase
import java.util.UUID

internal class PostsServerImpl(
    private val createPostUseCase: CreatePostUseCase,
    private val listFeedUseCase: ListFeedUseCase,
) : PostsServer {

    override suspend fun onCreatePost(request: CreatePostRequest): CreatePostResponse {
        val authorId: UUID = try {
            UUID.fromString(request.authorId)
        } catch (_: IllegalArgumentException) {
            return CreatePostResponse.ValidationFailure(
                message = "Author id must be a valid UUID and post text must not be blank.",
            )
        }
        return when (
            val result = createPostUseCase(
                authorId = authorId,
                text = request.text,
            )
        ) {
            is Result.Success -> CreatePostResponse.Success(post = result.post)
            is Result.ValidationFailure -> CreatePostResponse.ValidationFailure(
                message = "Author id must be valid and post text must not be blank.",
            )
            is Result.AuthorNotFound -> CreatePostResponse.NotFoundFailure(
                message = "Requested author was not found.",
            )
        }
    }

    override suspend fun onListFeed(request: ListFeedRequest): ListFeedResponse {
        return ListFeedResponse.Success(posts = listFeedUseCase().posts)
    }
}
