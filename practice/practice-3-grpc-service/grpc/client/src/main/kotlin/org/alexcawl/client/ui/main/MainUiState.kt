package org.alexcawl.client.ui.main

import org.alexcawl.contract.entity.Post

internal sealed interface MainUiState {

    data object Loading : MainUiState

    data class Content(
        val userId: String,
        val userName: String,
        val posts: List<Post>,
        val newPostText: String,
        val commentDrafts: Map<Long, String>,
        val isLoading: Boolean,
    ) : MainUiState
}
