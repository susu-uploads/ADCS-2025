package org.alexcawl.client.domain.main

internal sealed interface MainCommand

internal data class AuthorizeUserCommand(val userName: String) : MainCommand

internal data object LoadFeedCommand : MainCommand

internal data class CreatePostCommand(val authorId: String, val text: String) : MainCommand

internal data class LikePostCommand(val authorId: String, val postId: Long) : MainCommand

internal data class DislikePostCommand(val authorId: String, val postId: Long) : MainCommand

internal data class CommentPostCommand(val authorId: String, val postId: Long, val text: String) : MainCommand
