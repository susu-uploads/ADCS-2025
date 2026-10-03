package org.alexcawl.contract.entity

import org.alexcawl.contract.entity.proto.comment
import org.alexcawl.contract.entity.proto.failure
import org.alexcawl.contract.entity.proto.feed
import org.alexcawl.contract.entity.proto.post
import org.alexcawl.contract.entity.proto.user
import org.alexcawl.contract.entity.proto.Comment as ProtoComment
import org.alexcawl.contract.entity.proto.Failure as ProtoFailure
import org.alexcawl.contract.entity.proto.Feed as ProtoFeed
import org.alexcawl.contract.entity.proto.Post as ProtoPost
import org.alexcawl.contract.entity.proto.User as ProtoUser

internal fun ProtoPost.toModel(): Post {
    return Post(
        id = id,
        author = author.toModel(),
        text = text,
        createdAtEpochMillis = createdAtEpochMillis,
        likedBy = likedByList.map(ProtoUser::toModel),
        comments = commentsList.map(ProtoComment::toModel),
    )
}

internal fun ProtoComment.toModel(): Comment {
    return Comment(
        id = id,
        postId = postId,
        author = author.toModel(),
        text = text,
        createdAtEpochMillis = createdAtEpochMillis,
    )
}

internal fun ProtoUser.toModel(): User {
    return User(
        id = id,
        name = name,
    )
}

internal fun Post.toProto(): ProtoPost = post {
    id = this@toProto.id
    author = this@toProto.author.toProto()
    text = this@toProto.text
    createdAtEpochMillis = this@toProto.createdAtEpochMillis
    likedBy += this@toProto.likedBy.map(User::toProto)
    comments += this@toProto.comments.map(Comment::toProto)
}

internal fun Comment.toProto(): ProtoComment = comment {
    id = this@toProto.id
    postId = this@toProto.postId
    author = this@toProto.author.toProto()
    text = this@toProto.text
    createdAtEpochMillis = this@toProto.createdAtEpochMillis
}

internal fun User.toProto(): ProtoUser = user {
    id = this@toProto.id
    name = this@toProto.name
}

internal fun Failure.toProto(): ProtoFailure = failure {
    message = this@toProto.message
}

internal fun Feed.toProto(): ProtoFeed = feed{
    posts += this@toProto.posts.map(Post::toProto)
}
