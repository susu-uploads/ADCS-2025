package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User

@Serializable
public sealed interface ChatJoinResponse : ServerResponse {

    @Serializable
    public data class Success(val chat: Chat, val user: User) : ChatJoinResponse

    @Serializable
    public data object AlreadyInChat : ChatJoinResponse

    @Serializable
    public data object ChatNotFound : ChatJoinResponse

    @Serializable
    public data object UserNotFound : ChatJoinResponse
}
