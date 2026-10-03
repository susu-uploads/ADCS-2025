package org.alexcawl.contract.interactions

import org.alexcawl.contract.entity.Post

public sealed interface CommentPostResponse {

    public data class Success(
        public val post: Post,
    ) : CommentPostResponse

    public data class ValidationFailure(
        public val message: String,
    ) : CommentPostResponse

    public data class PostNotFound(
        public val message: String,
    ) : CommentPostResponse

    public data class AuthorNotFound(
        public val message: String,
    ) : CommentPostResponse
}
