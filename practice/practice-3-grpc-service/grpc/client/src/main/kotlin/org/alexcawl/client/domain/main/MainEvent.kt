package org.alexcawl.client.domain.main

import org.alexcawl.contract.entity.Post

internal sealed interface MainEvent

internal data class OnAuthorizedUserEvent(
    val userId: String,
    val userName: String,
) : MainEvent

internal data class OnAuthorizationFailedEvent(
    val message: String,
) : MainEvent

internal data class OnFeedLoadedEvent(val posts: List<Post>) : MainEvent

internal data class OnPostCreatedEvent(val post: Post) : MainEvent

internal data class OnPostUpdatedEvent(val post: Post) : MainEvent

internal data class OnMainFailureEvent(val message: String) : MainEvent

internal sealed interface MainUiEvent : MainEvent

internal data object OnRefreshClickedUiEvent : MainUiEvent

internal data class OnNewPostTextChangedUiEvent(val text: String) : MainUiEvent

internal data object OnCreatePostClickedUiEvent : MainUiEvent

internal data class OnLikeClickedUiEvent(val postId: Long) : MainUiEvent

internal data class OnDislikeClickedUiEvent(val postId: Long) : MainUiEvent

internal data class OnCommentTextChangedUiEvent(val postId: Long, val text: String) : MainUiEvent

internal data class OnCommentClickedUiEvent(val postId: Long) : MainUiEvent

internal data object OnLogoutClickedUiEvent : MainUiEvent
