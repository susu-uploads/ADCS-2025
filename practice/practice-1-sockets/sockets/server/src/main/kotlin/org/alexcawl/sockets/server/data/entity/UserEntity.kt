package org.alexcawl.sockets.server.data.entity

import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.data.table.Messages
import org.alexcawl.sockets.server.data.table.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*

internal class UserEntity(id: EntityID<UUID>) : UUIDEntity(id = id) {
    var name: String by Users.name
    var type: UserTypeEntity by Users.type
    var connectionType: UserConnectionTypeEntity by Users.connectionType

    val chats: SizedIterable<ChatEntity> by ChatEntity via ChatMembers
    val messages: SizedIterable<MessageEntity> by MessageEntity referrersOn Messages.authorId

    internal companion object : UUIDEntityClass<UserEntity>(table = Users)
}
