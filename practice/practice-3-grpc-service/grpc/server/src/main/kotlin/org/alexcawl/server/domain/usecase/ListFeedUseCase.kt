package org.alexcawl.server.domain.usecase

import org.alexcawl.contract.entity.Post

internal interface ListFeedUseCase {

    suspend operator fun invoke(): Result

    data class Result(val posts: List<Post>)
}
