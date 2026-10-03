package org.alexcawl.contract.interactions

import org.alexcawl.contract.entity.Post

public sealed interface LikePostResponse {

    public data class Success(
        public val post: Post,
    ) : LikePostResponse

    public data class ValidationFailure(
        public val message: String,
    ) : LikePostResponse

    public data class PostNotFound(
        public val message: String,
    ) : LikePostResponse

    public data class AuthorNotFound(
        public val message: String,
    ) : LikePostResponse

    public data class AlreadyLiked(
        public val message: String,
    ) : LikePostResponse
}
