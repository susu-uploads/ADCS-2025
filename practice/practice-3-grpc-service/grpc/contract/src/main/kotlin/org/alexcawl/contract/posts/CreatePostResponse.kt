package org.alexcawl.contract.posts

import org.alexcawl.contract.entity.Post

public sealed interface CreatePostResponse {

    public data class Success(
        public val post: Post,
    ) : CreatePostResponse

    public data class ValidationFailure(
        public val message: String,
    ) : CreatePostResponse

    public data class NotFoundFailure(
        public val message: String,
    ) : CreatePostResponse
}
