package org.alexcawl.server.data.table

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column

internal object Users : UUIDTable(name = "users") {
    val name: Column<String> = varchar(name = "name", length = 256)
}
