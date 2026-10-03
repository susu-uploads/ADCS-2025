package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.data.entity.PostEntity
import org.alexcawl.server.data.entity.PostLikeEntity
import org.alexcawl.server.data.entity.UserEntity
import org.alexcawl.server.domain.usecase.LikePostUseCase
import org.alexcawl.server.domain.usecase.LikePostUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class LikePostUseCaseImpl(
    private val database: Database,
) : LikePostUseCase {

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

        val alreadyLiked: Boolean = post.likes.any { like: PostLikeEntity ->
            like.author.id == author.id
        }

        if (alreadyLiked) {
            return Result.AlreadyLiked
        }

        PostLikeEntity.new {
            this.post = post
            this.author = author
            this.createdAt = System.currentTimeMillis()
        }

        return Result.Success(post = post.toContract())
    }
}
