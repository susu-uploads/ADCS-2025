package org.alexcawl.sockets.server.domain

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.entity.Message
import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.entity.UserConnectionType
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.entity.UserType
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.ChatTypeEntity
import org.alexcawl.sockets.server.data.entity.MessageEntity
import org.alexcawl.sockets.server.data.entity.MessageTypeEntity
import org.alexcawl.sockets.server.data.entity.UserConnectionTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.entity.UserTypeEntity

internal fun UserEntity.asDomain(): User {
    return User(
        id = id.value,
        name = name,
        type = type.asDomain(),
        connectionType = connectionType.asDomain(),
    )
}

internal fun UserTypeEntity.asDomain(): UserType {
    return when (this) {
        UserTypeEntity.SYSTEM -> UserType.SYSTEM
        UserTypeEntity.DEFAULT -> UserType.DEFAULT
    }
}

internal fun UserConnectionTypeEntity.asDomain(): UserConnectionType {
    return when (this) {
        UserConnectionTypeEntity.ONLINE -> UserConnectionType.ONLINE
        UserConnectionTypeEntity.OFFLINE -> UserConnectionType.OFFLINE
    }
}

internal fun ChatEntity.asDomain(): Chat {
    return Chat(
        id = id.value,
        name = name,
        type = type.asDomain(),
    )
}

internal fun ChatTypeEntity.asDomain(): ChatType {
    return when (this) {
        ChatTypeEntity.READ_ONLY -> ChatType.READ_ONLY
        ChatTypeEntity.DEFAULT -> ChatType.DEFAULT
    }
}

internal fun MessageEntity.asDomain(): Message {
    return Message(
        id = id.value,
        authorId = author.id.value,
        chatId = chat.id.value,
        text = text,
        timestamp = timestamp,
        type = type.asDomain(),
    )
}

internal fun MessageTypeEntity.asDomain(): MessageType {
    return when (this) {
        MessageTypeEntity.TEXT -> MessageType.TEXT
    }
}
