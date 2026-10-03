package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Message

@Serializable
public data class UpdateChatMessagesResponse(
    val chatId: Int,
    val messages: List<Message>,
) : UpdateResponse
