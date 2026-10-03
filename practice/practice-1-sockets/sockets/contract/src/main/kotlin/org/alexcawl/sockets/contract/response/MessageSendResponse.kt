package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.Message
import org.alexcawl.sockets.contract.entity.User

@Serializable
public sealed interface MessageSendResponse : ServerResponse {

    @Serializable
    public data class Success(val chat: Chat, val user: User, val message: Message) : MessageSendResponse

    @Serializable
    public data object ChatNotFound : MessageSendResponse

    @Serializable
    public data object UserNotFound : MessageSendResponse

    @Serializable
    public data object UserIsNotInChat : MessageSendResponse

    @Serializable
    public data object ChatIsReadOnly : MessageSendResponse
}
