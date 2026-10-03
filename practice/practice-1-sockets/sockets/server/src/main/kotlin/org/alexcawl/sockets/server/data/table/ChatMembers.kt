package org.alexcawl.sockets.server.data.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.util.UUID

internal object ChatMembers : Table(name = "chat_members") {
    val memberId: Column<EntityID<UUID>> = reference(name = "member_id", refColumn = Users.id, onDelete = ReferenceOption.CASCADE)
    val chatId: Column<EntityID<Int>> = reference(name = "chat_id", refColumn = Chats.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(firstColumn = memberId, chatId)
}
