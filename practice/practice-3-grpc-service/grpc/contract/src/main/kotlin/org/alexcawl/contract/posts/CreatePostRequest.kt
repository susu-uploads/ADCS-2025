package org.alexcawl.contract.posts

public data class CreatePostRequest(
    public val authorId: String,
    public val text: String,
)
