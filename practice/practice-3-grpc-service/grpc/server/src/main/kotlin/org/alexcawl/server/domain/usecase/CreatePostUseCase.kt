package org.alexcawl.server.domain.usecase

import org.alexcawl.contract.entity.Post

internal interface CreatePostUseCase {

    suspend operator fun invoke(authorId: java.util.UUID, text: String): Result

    sealed interface Result {

        data class Success(val post: Post) : Result

        data object ValidationFailure : Result

        data object AuthorNotFound : Result
    }
}
