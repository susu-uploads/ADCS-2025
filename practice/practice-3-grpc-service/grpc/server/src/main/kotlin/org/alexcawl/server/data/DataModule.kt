package org.alexcawl.server.data

import org.alexcawl.common.Container
import org.alexcawl.server.data.table.Comments
import org.alexcawl.server.data.table.PostLikes
import org.alexcawl.server.data.table.Posts
import org.alexcawl.server.data.table.Users
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
            SchemaUtils.create(Users, Posts, Comments, PostLikes)
        }
        database
    }
}
