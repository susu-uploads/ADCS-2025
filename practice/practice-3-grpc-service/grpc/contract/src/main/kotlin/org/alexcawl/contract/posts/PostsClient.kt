package org.alexcawl.contract.posts

import org.alexcawl.contract.posts.proto.PostsServiceGrpcKt

public class PostsClient(
    private val stub: PostsServiceGrpcKt.PostsServiceCoroutineStub,
) {

    public suspend fun createPost(request: CreatePostRequest): CreatePostResponse {
        return stub.createPost(request = request.toProto()).toModel()
    }

    public suspend fun listFeed(request: ListFeedRequest = ListFeedRequest()): ListFeedResponse {
        return stub.listFeed(request = request.toProto()).toModel()
    }
}
