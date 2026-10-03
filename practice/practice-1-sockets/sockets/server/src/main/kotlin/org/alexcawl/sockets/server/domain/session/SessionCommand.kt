package org.alexcawl.sockets.server.domain.session

import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.response.ServerResponse
import java.util.*

internal sealed interface SessionCommand

internal data class DecodeClientRequestCommand(val payload: String) : SessionCommand

internal data class EncodeServerResponseCommand(val response: ServerResponse, val lastAck: Boolean = false) : SessionCommand

internal data object SubscribeOnGlobalUpdatesCommand : SessionCommand

// region Contract

internal data class ChatCreateCommand(val userId: UUID, val chatName: String) : SessionCommand

internal data class ChatJoinCommand(val userId: UUID, val chatId: Int) : SessionCommand

internal data class ChatLeaveCommand(val userId: UUID, val chatId: Int) : SessionCommand

internal data class MessageSendCommand(val userId: UUID, val chatId: Int, val message: String, val type: MessageType) : SessionCommand

internal data class UserAuthorizeCommand(val userId: UUID, val userName: String?) : SessionCommand

internal data class UserChangeNameCommand(val userId: UUID, val userName: String) : SessionCommand

internal data class UserDeauthorizeCommand(val userId: UUID) : SessionCommand

internal data object UpdateChatsCommand : SessionCommand

internal data object UpdateUsersCommand : SessionCommand

internal data class UpdateChatMessagesCommand(val userId: UUID, val chatId: Int) : SessionCommand

internal data class UpdateChatMembersCommand(val userId: UUID, val chatId: Int) : SessionCommand

// endregion
