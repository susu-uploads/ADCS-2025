package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable

@Serializable
public data class ChatCreateRequest(val chatName: String) : ClientRequest
