package org.alexcawl.contract.interactions

import org.alexcawl.contract.interactions.proto.CommentPostRequest as ProtoCommentPostRequest
import org.alexcawl.contract.interactions.proto.CommentPostResponse as ProtoCommentPostResponse
import org.alexcawl.contract.interactions.proto.DislikePostRequest as ProtoDislikePostRequest
import org.alexcawl.contract.interactions.proto.DislikePostResponse as ProtoDislikePostResponse
import org.alexcawl.contract.interactions.proto.InteractionsServiceGrpcKt
import org.alexcawl.contract.interactions.proto.LikePostRequest as ProtoLikePostRequest
import org.alexcawl.contract.interactions.proto.LikePostResponse as ProtoLikePostResponse

public class InteractionsServerProto(
    private val server: InteractionsServer,
) : InteractionsServiceGrpcKt.InteractionsServiceCoroutineImplBase() {

    override suspend fun likePost(request: ProtoLikePostRequest): ProtoLikePostResponse {
        return server.onLikePost(request = request.toModel()).toProto()
    }

    override suspend fun dislikePost(request: ProtoDislikePostRequest): ProtoDislikePostResponse {
        return server.onDislikePost(request = request.toModel()).toProto()
    }

    override suspend fun commentPost(request: ProtoCommentPostRequest): ProtoCommentPostResponse {
        return server.onCommentPost(request = request.toModel()).toProto()
    }
}
