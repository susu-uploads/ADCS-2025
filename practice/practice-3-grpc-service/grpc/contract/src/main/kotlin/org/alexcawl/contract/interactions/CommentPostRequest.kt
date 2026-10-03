package org.alexcawl.contract.interactions

public data class CommentPostRequest(
    public val postId: Long,
    public val authorId: String,
    public val text: String,
)
