package org.alexcawl.server.data.entity

import org.alexcawl.server.data.table.Comments
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class CommentEntity(id: EntityID<Long>) : LongEntity(id = id) {
    var post: PostEntity by PostEntity referencedOn Comments.postId
    var author: UserEntity by UserEntity referencedOn Comments.authorId
    var text: String by Comments.text
    var createdAt: Long by Comments.createdAt

    internal companion object : LongEntityClass<CommentEntity>(table = Comments)
}
