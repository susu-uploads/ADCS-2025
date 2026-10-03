package org.alexcawl.contract.interactions

import org.alexcawl.contract.entity.Failure
import org.alexcawl.contract.entity.toModel
import org.alexcawl.contract.entity.toProto
import org.alexcawl.contract.interactions.proto.commentPostRequest
import org.alexcawl.contract.interactions.proto.dislikePostRequest
import org.alexcawl.contract.interactions.proto.likePostRequest
import org.alexcawl.contract.interactions.proto.CommentPostRequest as ProtoCommentPostRequest
import org.alexcawl.contract.interactions.proto.CommentPostResponse as ProtoCommentPostResponse
import org.alexcawl.contract.interactions.proto.DislikePostRequest as ProtoDislikePostRequest
import org.alexcawl.contract.interactions.proto.DislikePostResponse as ProtoDislikePostResponse
import org.alexcawl.contract.interactions.proto.LikePostRequest as ProtoLikePostRequest
import org.alexcawl.contract.interactions.proto.LikePostResponse as ProtoLikePostResponse
import org.alexcawl.contract.interactions.proto.commentPostResponse
import org.alexcawl.contract.interactions.proto.dislikePostResponse
import org.alexcawl.contract.interactions.proto.likePostResponse

internal fun LikePostRequest.toProto(): ProtoLikePostRequest = likePostRequest {
    postId = this@toProto.postId
    authorId = this@toProto.authorId
}

internal fun DislikePostRequest.toProto(): ProtoDislikePostRequest = dislikePostRequest {
    postId = this@toProto.postId
    authorId = this@toProto.authorId
}

internal fun CommentPostRequest.toProto(): ProtoCommentPostRequest = commentPostRequest {
    postId = this@toProto.postId
    authorId = this@toProto.authorId
    text = this@toProto.text
}

internal fun ProtoLikePostRequest.toModel(): LikePostRequest {
    return LikePostRequest(
        postId = postId,
        authorId = authorId,
    )
}

internal fun ProtoDislikePostRequest.toModel(): DislikePostRequest {
    return DislikePostRequest(
        postId = postId,
        authorId = authorId,
    )
}

internal fun ProtoCommentPostRequest.toModel(): CommentPostRequest {
    return CommentPostRequest(
        postId = postId,
        authorId = authorId,
        text = text,
    )
}

internal fun LikePostResponse.toProto(): ProtoLikePostResponse = likePostResponse {
    when (this@toProto) {
        is LikePostResponse.Success -> {
            post = this@toProto.post.toProto()
        }
        is LikePostResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
        is LikePostResponse.PostNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
        is LikePostResponse.AuthorNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
        is LikePostResponse.AlreadyLiked -> {
            conflictFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}

internal fun ProtoLikePostResponse.toModel(): LikePostResponse {
    return when (resultCase) {
        ProtoLikePostResponse.ResultCase.POST -> LikePostResponse.Success(post = post.toModel())
        ProtoLikePostResponse.ResultCase.VALIDATION_FAILURE -> {
            LikePostResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoLikePostResponse.ResultCase.NOT_FOUND_FAILURE -> {
            LikePostResponse.PostNotFound(message = notFoundFailure.message)
        }
        ProtoLikePostResponse.ResultCase.CONFLICT_FAILURE -> {
            LikePostResponse.AlreadyLiked(message = conflictFailure.message)
        }
        ProtoLikePostResponse.ResultCase.RESULT_NOT_SET -> {
            LikePostResponse.ValidationFailure(message = "LikePost returned no result.")
        }
    }
}

internal fun ProtoDislikePostResponse.toModel(): DislikePostResponse {
    return when (resultCase) {
        ProtoDislikePostResponse.ResultCase.POST -> DislikePostResponse.Success(post = post.toModel())
        ProtoDislikePostResponse.ResultCase.VALIDATION_FAILURE -> {
            DislikePostResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoDislikePostResponse.ResultCase.NOT_FOUND_FAILURE -> {
            DislikePostResponse.PostNotFound(message = notFoundFailure.message)
        }
        ProtoDislikePostResponse.ResultCase.CONFLICT_FAILURE -> {
            DislikePostResponse.NotLiked(message = conflictFailure.message)
        }
        ProtoDislikePostResponse.ResultCase.RESULT_NOT_SET -> {
            DislikePostResponse.ValidationFailure(message = "DislikePost returned no result.")
        }
    }
}

internal fun ProtoCommentPostResponse.toModel(): CommentPostResponse {
    return when (resultCase) {
        ProtoCommentPostResponse.ResultCase.POST -> CommentPostResponse.Success(post = post.toModel())
        ProtoCommentPostResponse.ResultCase.VALIDATION_FAILURE -> {
            CommentPostResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoCommentPostResponse.ResultCase.NOT_FOUND_FAILURE -> {
            CommentPostResponse.PostNotFound(message = notFoundFailure.message)
        }
        ProtoCommentPostResponse.ResultCase.RESULT_NOT_SET -> {
            CommentPostResponse.ValidationFailure(message = "CommentPost returned no result.")
        }
    }
}

internal fun DislikePostResponse.toProto(): ProtoDislikePostResponse = dislikePostResponse {
    when (this@toProto) {
        is DislikePostResponse.Success -> {
            post = this@toProto.post.toProto()
        }
        is DislikePostResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
        is DislikePostResponse.PostNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
        is DislikePostResponse.AuthorNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
        is DislikePostResponse.NotLiked -> {
            conflictFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}

internal fun CommentPostResponse.toProto(): ProtoCommentPostResponse = commentPostResponse {
    when (this@toProto) {
        is CommentPostResponse.Success -> {
            post = this@toProto.post.toProto()
        }
        is CommentPostResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
        is CommentPostResponse.PostNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
        is CommentPostResponse.AuthorNotFound -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}
