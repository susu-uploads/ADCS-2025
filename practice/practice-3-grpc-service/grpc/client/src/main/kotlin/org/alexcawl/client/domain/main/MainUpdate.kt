package org.alexcawl.client.domain.main
import org.alexcawl.contract.entity.Post
import org.alexcawl.kotea.DslUpdate

internal class MainUpdate : DslUpdate<MainState, MainEvent, MainCommand, MainNews>() {

    override fun NextBuilder.update(event: MainEvent) {
        when (event) {
            is OnAuthorizedUserEvent -> {
                state {
                    copy(
                        userId = event.userId,
                        userName = event.userName,
                        isLoading = true,
                    )
                }
                commands(LoadFeedCommand)
            }

            is OnAuthorizationFailedEvent -> {
                state { copy(isLoading = false) }
                news(OpenLoginScreenWithToast(message = event.message))
            }

            OnRefreshClickedUiEvent -> {
                if (state.userId == null) return
                state { copy(isLoading = true) }
                commands(LoadFeedCommand)
            }

            is OnNewPostTextChangedUiEvent -> state { copy(newPostText = event.text) }

            OnCreatePostClickedUiEvent -> {
                val userId: String = state.userId ?: return
                val postText: String = state.newPostText.trim()
                if (postText.isEmpty()) {
                    news(ShowBlankPostTextToast)
                    return
                }
                commands(
                    CreatePostCommand(
                        authorId = userId,
                        text = postText,
                    ),
                )
            }

            is OnLikeClickedUiEvent -> {
                val userId: String = state.userId ?: return
                commands(
                    LikePostCommand(
                        authorId = userId,
                        postId = event.postId,
                    ),
                )
            }

            is OnDislikeClickedUiEvent -> {
                val userId: String = state.userId ?: return
                commands(
                    DislikePostCommand(
                        authorId = userId,
                        postId = event.postId,
                    ),
                )
            }

            is OnCommentTextChangedUiEvent -> state {
                copy(commentDrafts = commentDrafts + (event.postId to event.text))
            }

            is OnCommentClickedUiEvent -> {
                val userId: String = state.userId ?: return
                val commentText: String = state.commentDrafts[event.postId].orEmpty().trim()
                if (commentText.isEmpty()) {
                    news(ShowBlankCommentTextToast)
                    return
                }
                commands(
                    CommentPostCommand(
                        authorId = userId,
                        postId = event.postId,
                        text = commentText,
                    ),
                )
            }

            OnLogoutClickedUiEvent -> news(OpenLoginScreen)

            is OnFeedLoadedEvent -> state {
                copy(posts = event.posts, isLoading = false)
            }

            is OnPostCreatedEvent -> state {
                copy(
                    posts = listOf(event.post) + posts.filterNot { post: Post -> post.id == event.post.id },
                    newPostText = "",
                    isLoading = false,
                )
            }

            is OnPostUpdatedEvent -> state {
                copy(
                    posts = posts.replace(post = event.post),
                    commentDrafts = commentDrafts - event.post.id,
                    isLoading = false,
                )
            }

            is OnMainFailureEvent -> {
                state { copy(isLoading = false) }
                news(ShowMainDynamicToast(message = event.message))
            }
        }
    }
}

private fun List<Post>.replace(post: Post): List<Post> {
    return map { current: Post ->
        if (current.id == post.id) post else current
    }
}
