package org.alexcawl.client.domain.main

import org.alexcawl.contract.entity.Post

internal data class MainState(
    val userId: String?,
    val userName: String,
    val posts: List<Post>,
    val newPostText: String,
    val commentDrafts: Map<Long, String>,
    val isLoading: Boolean,
)
