package org.alexcawl.server.data.entity

import org.alexcawl.server.data.table.Comments
import org.alexcawl.server.data.table.PostLikes
import org.alexcawl.server.data.table.Posts
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

internal class PostEntity(id: EntityID<Long>) : LongEntity(id = id) {
    var author: UserEntity by UserEntity referencedOn Posts.authorId
    var text: String by Posts.text
    var createdAt: Long by Posts.createdAt

    val comments: SizedIterable<CommentEntity> by CommentEntity referrersOn Comments.postId
    val likes: SizedIterable<PostLikeEntity> by PostLikeEntity referrersOn PostLikes.postId

    internal companion object : LongEntityClass<PostEntity>(table = Posts)
}
