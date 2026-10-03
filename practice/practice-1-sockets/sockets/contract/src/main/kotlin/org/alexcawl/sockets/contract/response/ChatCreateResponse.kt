package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Chat

@Serializable
public sealed interface ChatCreateResponse : ServerResponse {

    @Serializable
    public data class Success(val chat: Chat) : ChatCreateResponse

    @Serializable
    public data object UserNotFound : ChatCreateResponse
}
