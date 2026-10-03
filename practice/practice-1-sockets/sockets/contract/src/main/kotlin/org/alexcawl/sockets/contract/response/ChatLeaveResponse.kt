package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User

@Serializable
public sealed interface ChatLeaveResponse : ServerResponse {

    @Serializable
    public data class Success(val chat: Chat, val user: User) : ChatLeaveResponse

    @Serializable
    public data object AlreadyNotInChat : ChatLeaveResponse

    @Serializable
    public data object ChatNotFound : ChatLeaveResponse

    @Serializable
    public data object UserNotFound : ChatLeaveResponse
}
