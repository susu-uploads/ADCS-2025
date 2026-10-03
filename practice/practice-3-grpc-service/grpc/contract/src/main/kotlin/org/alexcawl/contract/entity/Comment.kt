package org.alexcawl.contract.entity

public data class Comment(
    public val id: Long,
    public val postId: Long,
    public val author: User,
    public val text: String,
    public val createdAtEpochMillis: Long,
)
