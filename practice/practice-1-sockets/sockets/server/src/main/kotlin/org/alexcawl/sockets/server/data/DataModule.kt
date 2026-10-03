package org.alexcawl.sockets.server.data

import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.data.table.Chats
import org.alexcawl.sockets.server.data.table.Messages
import org.alexcawl.sockets.server.data.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

internal class DataModule(
    private val databaseUrl: String,
    private val databaseDriver: String,
    private val databaseUser: String?,
    private val databasePassword: String?,
) : Container {

    val database: Database by single {
        val database: Database = Database.connect(
            url = databaseUrl,
            driver = databaseDriver,
            user = databaseUser.orEmpty(),
            password = databasePassword.orEmpty(),
        )
        transaction(db = database) {
            SchemaUtils.create(Users, Chats, Messages, ChatMembers)
        }
        database
    }
}
