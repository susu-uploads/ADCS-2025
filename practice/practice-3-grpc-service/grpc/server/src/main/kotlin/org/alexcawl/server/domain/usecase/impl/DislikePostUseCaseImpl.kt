package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.data.entity.PostEntity
import org.alexcawl.server.data.entity.PostLikeEntity
import org.alexcawl.server.data.entity.UserEntity
import org.alexcawl.server.domain.usecase.DislikePostUseCase
import org.alexcawl.server.domain.usecase.DislikePostUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class DislikePostUseCaseImpl(
    private val database: Database,
) : DislikePostUseCase {

    override suspend fun invoke(postId: Long, authorId: UUID): Result {
        return newSuspendedTransaction(db = database) {
            operation(postId = postId, authorId = authorId)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.operation(postId: Long, authorId: UUID): Result {
        if (postId <= 0L) {
            return Result.ValidationFailure
        }

        val author = UserEntity.findById(authorId)
            ?: return Result.AuthorNotFound

        val post: PostEntity = PostEntity.findById(postId)
            ?: return Result.PostNotFound

        val existingLike: PostLikeEntity = post.likes.firstOrNull { like ->
            like.author.id == author.id
        } ?: return Result.NotLiked

        existingLike.delete()

        return Result.Success(post = post.toContract())
    }
}
