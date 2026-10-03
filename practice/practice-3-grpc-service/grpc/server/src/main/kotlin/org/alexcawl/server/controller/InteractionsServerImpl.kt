package org.alexcawl.server.controller

import org.alexcawl.contract.interactions.CommentPostRequest
import org.alexcawl.contract.interactions.CommentPostResponse
import org.alexcawl.contract.interactions.DislikePostRequest
import org.alexcawl.contract.interactions.DislikePostResponse
import org.alexcawl.contract.interactions.InteractionsServer
import org.alexcawl.contract.interactions.LikePostRequest
import org.alexcawl.contract.interactions.LikePostResponse
import org.alexcawl.server.domain.usecase.CommentPostUseCase
import org.alexcawl.server.domain.usecase.DislikePostUseCase
import org.alexcawl.server.domain.usecase.LikePostUseCase
import java.util.UUID

internal class InteractionsServerImpl(
    private val likePostUseCase: LikePostUseCase,
    private val dislikePostUseCase: DislikePostUseCase,
    private val commentPostUseCase: CommentPostUseCase,
) : InteractionsServer {

    private fun parseAuthorId(authorId: String): UUID? {
        return try {
            UUID.fromString(authorId)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override suspend fun onLikePost(request: LikePostRequest): LikePostResponse {
        val authorId: UUID = parseAuthorId(request.authorId)
            ?: return LikePostResponse.ValidationFailure(
                message = "Post id must be positive and author id must be a valid UUID.",
            )

        return when (
            val result = likePostUseCase(
                postId = request.postId,
                authorId = authorId,
            )
        ) {
            is LikePostUseCase.Result.Success -> LikePostResponse.Success(post = result.post)
            is LikePostUseCase.Result.ValidationFailure -> LikePostResponse.ValidationFailure(
                message = "Post id must be positive and author id must be valid.",
            )
            is LikePostUseCase.Result.PostNotFound -> LikePostResponse.PostNotFound(
                message = "Requested post was not found.",
            )
            is LikePostUseCase.Result.AuthorNotFound -> LikePostResponse.AuthorNotFound(
                message = "Requested author was not found.",
            )
            is LikePostUseCase.Result.AlreadyLiked -> LikePostResponse.AlreadyLiked(
                message = "This post is already liked by the specified author.",
            )
        }
    }

    override suspend fun onDislikePost(request: DislikePostRequest): DislikePostResponse {
        val authorId: UUID = parseAuthorId(request.authorId)
            ?: return DislikePostResponse.ValidationFailure(
                message = "Post id must be positive and author id must be a valid UUID.",
            )

        return when (
            val result = dislikePostUseCase(
                postId = request.postId,
                authorId = authorId,
            )
        ) {
            is DislikePostUseCase.Result.Success -> DislikePostResponse.Success(post = result.post)
            DislikePostUseCase.Result.ValidationFailure -> DislikePostResponse.ValidationFailure(
                message = "Post id must be positive and author id must be valid.",
            )
            DislikePostUseCase.Result.PostNotFound -> DislikePostResponse.PostNotFound(
                message = "Requested post was not found.",
            )
            DislikePostUseCase.Result.AuthorNotFound -> DislikePostResponse.AuthorNotFound(
                message = "Requested author was not found.",
            )
            DislikePostUseCase.Result.NotLiked -> DislikePostResponse.NotLiked(
                message = "This post is not liked by the specified author.",
            )
        }
    }

    override suspend fun onCommentPost(request: CommentPostRequest): CommentPostResponse {
        val authorId: UUID = parseAuthorId(request.authorId)
            ?: return CommentPostResponse.ValidationFailure(
                message = "Post id must be positive, author id must be a valid UUID, and comment text must not be blank.",
            )

        return when (
            val result = commentPostUseCase(
                postId = request.postId,
                authorId = authorId,
                text = request.text,
            )
        ) {
            is CommentPostUseCase.Result.Success -> CommentPostResponse.Success(post = result.post)
            CommentPostUseCase.Result.ValidationFailure -> CommentPostResponse.ValidationFailure(
                message = "Post id must be positive, author id must be valid, and comment text must not be blank.",
            )
            CommentPostUseCase.Result.PostNotFound -> CommentPostResponse.PostNotFound(
                message = "Requested post was not found.",
            )
            CommentPostUseCase.Result.AuthorNotFound -> CommentPostResponse.AuthorNotFound(
                message = "Requested author was not found.",
            )
        }
    }
}
