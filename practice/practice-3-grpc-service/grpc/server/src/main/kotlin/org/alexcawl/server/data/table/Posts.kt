package org.alexcawl.server.data.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

internal object Posts : LongIdTable(name = "posts") {
    val authorId = reference(
        name = "author_id",
        refColumn = Users.id,
        onDelete = ReferenceOption.RESTRICT,
    )
    val text: Column<String> = text(name = "text")
    val createdAt: Column<Long> = long(name = "created_at")
}
