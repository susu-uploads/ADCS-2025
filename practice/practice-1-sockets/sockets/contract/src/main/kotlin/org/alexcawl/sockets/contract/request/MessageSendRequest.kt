package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.MessageType

@Serializable
public data class MessageSendRequest(val chatId: Int, val message: String, val messageType: MessageType) : ClientRequest
