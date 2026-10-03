package org.alexcawl.sockets.client.domain.main

import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.request.ClientRequest
import java.util.UUID

internal sealed interface MainCommand


internal data class DecodeServerResponseCommand(val payload: String) : MainCommand

internal data class EncodeClientRequestCommand(val request: ClientRequest, val lastAck: Boolean = false) : MainCommand

internal data class SendNetworkMessageCommand(val payload: String, val lastAck: Boolean = false) : MainCommand

internal data object DisconnectNetworkCommand : MainCommand

internal data object SubscribeOnNetworkMessagesCommand : MainCommand

// region Contract

internal data class ChatCreateCommand(val userId: UUID, val chatName: String) : MainCommand

internal data class ChatJoinCommand(val userId: UUID, val chatId: Int) : MainCommand

internal data class ChatLeaveCommand(val userId: UUID, val chatId: Int) : MainCommand

internal data class MessageSendCommand(val userId: UUID, val chatId: Int, val message: String, val type: MessageType) : MainCommand

internal data class UserAuthorizeCommand(val userId: UUID?, val userName: String?) : MainCommand

internal data class UserChangeNameCommand(val userId: UUID, val userName: String) : MainCommand

internal data class UserDeauthorizeCommand(val userId: UUID) : MainCommand

// endregion
