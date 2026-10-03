package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.server.data.entity.UserEntity
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase.Result
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class AuthorizeUserUseCaseImpl(
    private val database: Database,
) : AuthorizeUserUseCase {

    override suspend fun invoke(name: String): Result {
        return newSuspendedTransaction(db = database) {
            operation(name = name)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.operation(name: String): Result {
        val normalizedName: String = name.trim()
        if (normalizedName.isEmpty()) {
            return Result.ValidationFailure
        }

        val user = UserEntity.new(id = UUID.randomUUID()) {
            this.name = normalizedName
        }

        return Result.Success(user = user.toContract())
    }
}
