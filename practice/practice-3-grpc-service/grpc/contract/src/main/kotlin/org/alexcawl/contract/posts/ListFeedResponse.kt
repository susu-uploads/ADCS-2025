package org.alexcawl.contract.posts

import org.alexcawl.contract.entity.Post

public sealed interface ListFeedResponse {

    public data class Success(
        public val posts: List<Post>,
    ) : ListFeedResponse

    public data class ValidationFailure(
        public val message: String,
    ) : ListFeedResponse
}
