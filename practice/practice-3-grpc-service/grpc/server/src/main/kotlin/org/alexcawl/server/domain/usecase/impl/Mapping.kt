package org.alexcawl.server.domain.usecase.impl

import org.alexcawl.contract.entity.Comment
import org.alexcawl.contract.entity.Post
import org.alexcawl.contract.entity.User
import org.alexcawl.server.data.entity.CommentEntity
import org.alexcawl.server.data.entity.PostEntity
import org.alexcawl.server.data.entity.PostLikeEntity
import org.alexcawl.server.data.entity.UserEntity

internal fun UserEntity.toContract(): User {
    return User(
        id = id.value.toString(),
        name = name,
    )
}

internal fun PostEntity.toContract(): Post {
    return Post(
        id = id.value,
        author = author.toContract(),
        text = text,
        createdAtEpochMillis = createdAt,
        likedBy = likes
            .toList()
            .map(PostLikeEntity::author)
            .map(UserEntity::toContract),
        comments = comments
            .toList()
            .map(CommentEntity::toContract),
    )
}

internal fun CommentEntity.toContract(): Comment {
    return Comment(
        id = id.value,
        postId = post.id.value,
        author = author.toContract(),
        text = text,
        createdAtEpochMillis = createdAt,
    )
}
