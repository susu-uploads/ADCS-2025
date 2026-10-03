package org.alexcawl.sockets.server.data.table

import org.alexcawl.sockets.server.data.entity.MessageTypeEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

internal object Messages : IntIdTable(name = "messages") {
    val authorId: Column<EntityID<UUID>> = reference(name = "author_id", refColumn = Users.id, onDelete = ReferenceOption.CASCADE)
    val chatId: Column<EntityID<Int>> = reference(name = "chat_id", refColumn = Chats.id, onDelete = ReferenceOption.CASCADE)
    val text: Column<String> = text(name = "text")
    val timestamp: Column<Long> = long(name = "timestamp")
    val type: Column<MessageTypeEntity> = enumeration(name = "type", klass = MessageTypeEntity::class)
}
