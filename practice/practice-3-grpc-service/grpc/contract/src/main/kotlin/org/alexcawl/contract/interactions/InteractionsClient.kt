package org.alexcawl.contract.interactions

import org.alexcawl.contract.interactions.proto.InteractionsServiceGrpcKt

public class InteractionsClient(
    private val stub: InteractionsServiceGrpcKt.InteractionsServiceCoroutineStub,
) {

    public suspend fun likePost(request: LikePostRequest): LikePostResponse {
        return stub.likePost(request = request.toProto()).toModel()
    }

    public suspend fun dislikePost(request: DislikePostRequest): DislikePostResponse {
        return stub.dislikePost(request = request.toProto()).toModel()
    }

    public suspend fun commentPost(request: CommentPostRequest): CommentPostResponse {
        return stub.commentPost(request = request.toProto()).toModel()
    }
}
