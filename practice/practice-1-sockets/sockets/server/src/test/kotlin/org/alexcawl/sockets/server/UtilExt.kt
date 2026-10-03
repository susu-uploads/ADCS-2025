package org.alexcawl.sockets.server

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.response.UpdateChatsResponse

internal fun UpdateChatsResponse.findSystemChat(systemChatName: String): Chat {
    return chats.single { chat: Chat ->
        chat.name == systemChatName && chat.type == ChatType.READ_ONLY
    }
}
