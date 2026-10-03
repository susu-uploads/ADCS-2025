package org.alexcawl.contract.posts

import org.alexcawl.contract.posts.proto.PostsServiceGrpcKt
import org.alexcawl.contract.posts.proto.CreatePostRequest as ProtoCreatePostRequest
import org.alexcawl.contract.posts.proto.CreatePostResponse as ProtoCreatePostResponse
import org.alexcawl.contract.posts.proto.ListFeedRequest as ProtoListFeedRequest
import org.alexcawl.contract.posts.proto.ListFeedResponse as ProtoListFeedResponse

public class PostsServerProto(
    private val server: PostsServer
) : PostsServiceGrpcKt.PostsServiceCoroutineImplBase() {

    override suspend fun createPost(request: ProtoCreatePostRequest): ProtoCreatePostResponse {
        return server.onCreatePost(request = request.toModel()).toProto()
    }

    override suspend fun listFeed(request: ProtoListFeedRequest): ProtoListFeedResponse {
        return server.onListFeed(request = request.toModel()).toProto()
    }
}
