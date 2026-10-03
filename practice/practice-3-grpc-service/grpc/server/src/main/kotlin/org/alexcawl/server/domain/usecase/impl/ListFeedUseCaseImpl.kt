package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.data.entity.PostEntity
import org.alexcawl.server.domain.usecase.ListFeedUseCase
import org.alexcawl.server.domain.usecase.ListFeedUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal class ListFeedUseCaseImpl(
    private val database: Database,
) : ListFeedUseCase {

    override suspend fun invoke(): Result {
        return newSuspendedTransaction(db = database) {
            operation()
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.operation(): Result {
        return Result(
            posts = PostEntity.all()
                .toList()
                .sortedWith(compareByDescending { it.createdAt })
                .map(PostEntity::toContract),
        )
    }
}
