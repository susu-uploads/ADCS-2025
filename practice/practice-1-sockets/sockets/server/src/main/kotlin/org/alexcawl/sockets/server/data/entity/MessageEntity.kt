package org.alexcawl.sockets.server.data.entity

import org.alexcawl.sockets.server.data.table.Messages
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class MessageEntity(id: EntityID<Int>) : IntEntity(id = id) {
    var author: UserEntity by UserEntity referencedOn Messages.authorId
    var chat: ChatEntity by ChatEntity referencedOn Messages.chatId
    var text: String by Messages.text
    var timestamp: Long by Messages.timestamp
    var type: MessageTypeEntity by Messages.type

    internal companion object : IntEntityClass<MessageEntity>(table = Messages)
}
