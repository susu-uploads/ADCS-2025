package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable

@Serializable
public data class ChatLeaveRequest(val chatId: Int) : ClientRequest
