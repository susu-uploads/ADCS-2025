package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.domain.usecase.CreatePostUseCase
import org.alexcawl.server.data.entity.UserEntity
import org.alexcawl.server.domain.usecase.CreatePostUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class CreatePostUseCaseImpl(
    private val database: Database,
) : CreatePostUseCase {

    override suspend fun invoke(authorId: UUID, text: String): Result {
        return newSuspendedTransaction(db = database) {
            operation(authorId = authorId, text = text)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.operation(authorId: UUID, text: String): Result {
        val normalizedText: String = text.trim()

        if (normalizedText.isEmpty()) {
            return Result.ValidationFailure
        }

        val author = UserEntity.findById(authorId)
            ?: return Result.AuthorNotFound

        val post = org.alexcawl.server.data.entity.PostEntity.new {
            this.author = author
            this.text = normalizedText
            this.createdAt = System.currentTimeMillis()
        }

        return Result.Success(post = post.toContract())
    }
}
