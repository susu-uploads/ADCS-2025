package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable

@Serializable
public data class ChatJoinRequest(val chatId: Int) : ClientRequest
