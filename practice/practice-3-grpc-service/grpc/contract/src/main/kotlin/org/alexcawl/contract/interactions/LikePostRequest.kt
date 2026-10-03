package org.alexcawl.contract.interactions

public data class LikePostRequest(
    public val postId: Long,
    public val authorId: String,
)
