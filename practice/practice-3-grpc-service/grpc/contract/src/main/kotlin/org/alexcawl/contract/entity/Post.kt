package org.alexcawl.contract.entity

public data class Post(
    public val id: Long,
    public val author: User,
    public val text: String,
    public val createdAtEpochMillis: Long,
    public val likedBy: List<User>,
    public val comments: List<Comment>,
)
