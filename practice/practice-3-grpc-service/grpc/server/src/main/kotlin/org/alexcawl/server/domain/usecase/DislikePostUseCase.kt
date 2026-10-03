package org.alexcawl.server.domain.usecase

import org.alexcawl.contract.entity.Post

internal interface DislikePostUseCase {

    suspend operator fun invoke(postId: Long, authorId: java.util.UUID): Result

    sealed interface Result {

        data class Success(val post: Post) : Result

        data object ValidationFailure : Result

        data object PostNotFound : Result

        data object AuthorNotFound : Result

        data object NotLiked : Result
    }
}
