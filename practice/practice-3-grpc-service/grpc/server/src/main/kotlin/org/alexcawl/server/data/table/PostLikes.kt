package org.alexcawl.server.data.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

internal object PostLikes : LongIdTable(name = "post_likes") {
    val postId: Column<EntityID<Long>> = reference(
        name = "post_id",
        refColumn = Posts.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val authorId = reference(
        name = "author_id",
        refColumn = Users.id,
        onDelete = ReferenceOption.RESTRICT,
    )
    val createdAt: Column<Long> = long(name = "created_at")

    init {
        uniqueIndex(postId, authorId)
    }
}
