package org.alexcawl.server.domain.usecase

import org.alexcawl.contract.entity.Post
import java.util.UUID

internal interface LikePostUseCase {

    suspend operator fun invoke(postId: Long, authorId: UUID): Result

    sealed interface Result {

        data class Success(val post: Post) : Result

        data object ValidationFailure : Result

        data object PostNotFound : Result

        data object AuthorNotFound : Result

        data object AlreadyLiked : Result
    }
}
