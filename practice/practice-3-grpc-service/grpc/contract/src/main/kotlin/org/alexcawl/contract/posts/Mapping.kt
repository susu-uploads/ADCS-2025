package org.alexcawl.contract.posts

import org.alexcawl.contract.entity.Failure
import org.alexcawl.contract.entity.Feed
import org.alexcawl.contract.entity.toModel
import org.alexcawl.contract.entity.toProto
import org.alexcawl.contract.posts.proto.createPostRequest
import org.alexcawl.contract.posts.proto.listFeedRequest
import org.alexcawl.contract.posts.proto.CreatePostRequest as ProtoCreatePostRequest
import org.alexcawl.contract.posts.proto.CreatePostResponse as ProtoCreatePostResponse
import org.alexcawl.contract.posts.proto.ListFeedRequest as ProtoListFeedRequest
import org.alexcawl.contract.posts.proto.ListFeedResponse as ProtoListFeedResponse
import org.alexcawl.contract.posts.proto.createPostResponse
import org.alexcawl.contract.posts.proto.listFeedResponse

internal fun CreatePostRequest.toProto(): ProtoCreatePostRequest = createPostRequest {
    authorId = this@toProto.authorId
    text = this@toProto.text
}

internal fun ListFeedRequest.toProto(): ProtoListFeedRequest = listFeedRequest {}

internal fun ProtoCreatePostRequest.toModel(): CreatePostRequest {
    return CreatePostRequest(
        authorId = authorId,
        text = text,
    )
}

internal fun ProtoListFeedRequest.toModel(): ListFeedRequest {
    return ListFeedRequest()
}

internal fun CreatePostResponse.toProto(): ProtoCreatePostResponse = createPostResponse {
    when (this@toProto) {
        is CreatePostResponse.Success -> {
            post = this@toProto.post.toProto()
        }
        is CreatePostResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
        is CreatePostResponse.NotFoundFailure -> {
            notFoundFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}

internal fun ProtoCreatePostResponse.toModel(): CreatePostResponse {
    return when (resultCase) {
        ProtoCreatePostResponse.ResultCase.POST -> CreatePostResponse.Success(post = post.toModel())
        ProtoCreatePostResponse.ResultCase.VALIDATION_FAILURE -> {
            CreatePostResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoCreatePostResponse.ResultCase.NOT_FOUND_FAILURE -> {
            CreatePostResponse.NotFoundFailure(message = notFoundFailure.message)
        }
        ProtoCreatePostResponse.ResultCase.RESULT_NOT_SET -> {
            CreatePostResponse.ValidationFailure(message = "CreatePost returned no result.")
        }
    }
}

internal fun ProtoListFeedResponse.toModel(): ListFeedResponse {
    return when (resultCase) {
        ProtoListFeedResponse.ResultCase.FEED -> {
            ListFeedResponse.Success(posts = feed.postsList.map { post -> post.toModel() })
        }
        ProtoListFeedResponse.ResultCase.VALIDATION_FAILURE -> {
            ListFeedResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoListFeedResponse.ResultCase.RESULT_NOT_SET -> {
            ListFeedResponse.ValidationFailure(message = "ListFeed returned no result.")
        }
    }
}

internal fun ListFeedResponse.toProto(): ProtoListFeedResponse = listFeedResponse {
    when (this@toProto) {
        is ListFeedResponse.Success -> {
            feed = Feed(posts = this@toProto.posts).toProto()
        }
        is ListFeedResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}
