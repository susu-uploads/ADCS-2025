package org.alexcawl.sockets.server.data.table

import org.alexcawl.sockets.server.data.entity.UserConnectionTypeEntity
import org.alexcawl.sockets.server.data.entity.UserTypeEntity
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column

internal object Users : UUIDTable(name = "users") {
    val name: Column<String> = varchar(name = "name", length = 256)
    val type: Column<UserTypeEntity> = enumeration(name = "type", klass = UserTypeEntity::class)
    val connectionType: Column<UserConnectionTypeEntity> = enumeration(name = "connection_type", klass = UserConnectionTypeEntity::class)
}
