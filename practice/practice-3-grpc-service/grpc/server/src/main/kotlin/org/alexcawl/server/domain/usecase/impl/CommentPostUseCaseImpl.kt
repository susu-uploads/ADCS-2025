package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.data.entity.CommentEntity
import org.alexcawl.server.data.entity.PostEntity
import org.alexcawl.server.data.entity.UserEntity
import org.alexcawl.server.domain.usecase.CommentPostUseCase
import org.alexcawl.server.domain.usecase.CommentPostUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class CommentPostUseCaseImpl(
    private val database: Database,
) : CommentPostUseCase {

    override suspend fun invoke(postId: Long, authorId: UUID, text: String): Result {
        return newSuspendedTransaction(db = database) {
            operation(postId = postId, authorId = authorId, text = text)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.operation(postId: Long, authorId: UUID, text: String): Result {
        if (postId <= 0L) {
            return Result.ValidationFailure
        }

        val normalizedText: String = text.trim()

        if (normalizedText.isEmpty()) {
            return Result.ValidationFailure
        }

        val author = UserEntity.findById(authorId)
            ?: return Result.AuthorNotFound

        val post: PostEntity = PostEntity.findById(postId)
            ?: return Result.PostNotFound

        CommentEntity.new {
            this.post = post
            this.author = author
            this.text = normalizedText
            this.createdAt = System.currentTimeMillis()
        }

        return Result.Success(post = post.toContract())
    }
}
