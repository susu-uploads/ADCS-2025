package org.alexcawl.sockets.server.data.table

import org.alexcawl.sockets.server.data.entity.ChatTypeEntity
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

internal object Chats : IntIdTable(name = "chats") {
    val name: Column<String> = varchar(name = "name", length = 256)
    val type: Column<ChatTypeEntity> = enumeration(name = "type", klass = ChatTypeEntity::class)
}
