package org.alexcawl.server.data.entity

import org.alexcawl.server.data.table.Comments
import org.alexcawl.server.data.table.PostLikes
import org.alexcawl.server.data.table.Posts
import org.alexcawl.server.data.table.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

internal class UserEntity(id: EntityID<UUID>) : UUIDEntity(id = id) {
    var name: String by Users.name

    val posts: SizedIterable<PostEntity> by PostEntity referrersOn Posts.authorId
    val comments: SizedIterable<CommentEntity> by CommentEntity referrersOn Comments.authorId
    val likes: SizedIterable<PostLikeEntity> by PostLikeEntity referrersOn PostLikes.authorId

    internal companion object : UUIDEntityClass<UserEntity>(table = Users)
}
