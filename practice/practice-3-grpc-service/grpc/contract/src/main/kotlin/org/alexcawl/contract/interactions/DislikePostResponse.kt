package org.alexcawl.contract.interactions

import org.alexcawl.contract.entity.Post

public sealed interface DislikePostResponse {

    public data class Success(
        public val post: Post,
    ) : DislikePostResponse

    public data class ValidationFailure(
        public val message: String,
    ) : DislikePostResponse

    public data class PostNotFound(
        public val message: String,
    ) : DislikePostResponse

    public data class AuthorNotFound(
        public val message: String,
    ) : DislikePostResponse

    public data class NotLiked(
        public val message: String,
    ) : DislikePostResponse
}
