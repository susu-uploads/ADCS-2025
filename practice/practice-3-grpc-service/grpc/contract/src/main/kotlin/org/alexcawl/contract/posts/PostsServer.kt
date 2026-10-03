package org.alexcawl.contract.posts

public interface PostsServer {

    public suspend fun onCreatePost(request: CreatePostRequest): CreatePostResponse

    public suspend fun onListFeed(request: ListFeedRequest): ListFeedResponse
}
