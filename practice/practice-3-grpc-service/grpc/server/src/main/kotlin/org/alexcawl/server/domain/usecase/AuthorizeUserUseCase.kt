package org.alexcawl.server.domain.usecase

import org.alexcawl.contract.entity.User

internal interface AuthorizeUserUseCase {

    suspend operator fun invoke(name: String): Result

    sealed interface Result {

        data class Success(val user: User) : Result

        data object ValidationFailure : Result
    }
}
