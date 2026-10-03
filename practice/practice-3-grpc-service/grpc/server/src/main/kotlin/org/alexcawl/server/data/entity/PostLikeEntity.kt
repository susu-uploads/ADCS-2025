package org.alexcawl.server.data.entity

import org.alexcawl.server.data.table.PostLikes
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class PostLikeEntity(id: EntityID<Long>) : LongEntity(id = id) {
    var post: PostEntity by PostEntity referencedOn PostLikes.postId
    var author: UserEntity by UserEntity referencedOn PostLikes.authorId
    var createdAt: Long by PostLikes.createdAt

    internal companion object : LongEntityClass<PostLikeEntity>(table = PostLikes)
}
