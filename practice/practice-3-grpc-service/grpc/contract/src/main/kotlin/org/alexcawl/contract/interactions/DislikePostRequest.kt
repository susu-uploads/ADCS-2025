package org.alexcawl.contract.interactions

public data class DislikePostRequest(
    public val postId: Long,
    public val authorId: String,
)
