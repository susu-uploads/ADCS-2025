package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Chat

@Serializable
public data class UpdateChatsResponse(val chats: List<Chat>) : UpdateResponse
