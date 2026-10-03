package org.alexcawl.sockets.server.data.entity

import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.data.table.Chats
import org.alexcawl.sockets.server.data.table.Messages
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

internal class ChatEntity(id: EntityID<Int>) : IntEntity(id = id) {
    var name: String by Chats.name
    var type: ChatTypeEntity by Chats.type

    val members: SizedIterable<UserEntity> by UserEntity via ChatMembers
    val messages: SizedIterable<MessageEntity> by MessageEntity referrersOn Messages.chatId

    internal companion object : IntEntityClass<ChatEntity>(table = Chats)
}
