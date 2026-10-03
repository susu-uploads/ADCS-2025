package org.alexcawl.contract.interactions

public interface InteractionsServer {

    public suspend fun onLikePost(request: LikePostRequest): LikePostResponse

    public suspend fun onDislikePost(request: DislikePostRequest): DislikePostResponse

    public suspend fun onCommentPost(request: CommentPostRequest): CommentPostResponse
}
