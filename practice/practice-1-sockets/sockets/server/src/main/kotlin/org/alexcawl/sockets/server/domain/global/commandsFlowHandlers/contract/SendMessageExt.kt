package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.MessageEntity
import org.alexcawl.sockets.server.data.entity.MessageTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.jetbrains.exposed.sql.Transaction
import java.util.UUID

@Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
internal suspend fun Transaction.sendMessage(authorId: UUID, chatId: Int, message: String) {
    val authorEntity: UserEntity? = UserEntity.findById(id = authorId)
    val chatEntity: ChatEntity? = ChatEntity.findById(id = chatId)
    if (authorEntity != null && chatEntity != null) {
        MessageEntity.new {
            author = authorEntity
            chat = chatEntity
            timestamp = generateTimestamp()
            text = message
            type = MessageTypeEntity.TEXT
        }
    }
}
