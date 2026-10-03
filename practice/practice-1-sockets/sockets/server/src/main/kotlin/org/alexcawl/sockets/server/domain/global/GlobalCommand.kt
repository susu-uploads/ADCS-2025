package org.alexcawl.sockets.server.domain.global

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User
import java.util.UUID

internal sealed interface GlobalCommand

internal data object CreateSystemUserCommand : GlobalCommand

internal data class CreateSystemChatCommand(val systemUserId: UUID) : GlobalCommand

// region Contract

internal data class ChatCreateCommand(
    val systemUserId: UUID?,
    val chat: Chat,
    val timestamp: Long,
) : GlobalCommand

internal data class ChatJoinCommand(
    val systemUserId: UUID?,
    val user: User,
    val chat: Chat,
    val timestamp: Long,
) : GlobalCommand

internal data class ChatLeaveCommand(
    val systemUserId: UUID?,
    val user: User,
    val chat: Chat,
    val timestamp: Long,
) : GlobalCommand

internal data class MessageSendCommand(
    val chat: Chat,
) : GlobalCommand

internal data class UserAuthorizeCommand(
    val systemUserId: UUID?,
    val systemChatId: Int?,
    val user: User,
    val timestamp: Long,
) : GlobalCommand

internal data class UserChangeNameCommand(
    val systemUserId: UUID?,
    val systemChatId: Int?,
    val oldUserName: String?,
    val newUserName: String,
    val timestamp: Long,
) : GlobalCommand

internal data class UserDeauthorizeCommand(
    val systemUserId: UUID?,
    val systemChatId: Int?,
    val user: User,
    val timestamp: Long,
) : GlobalCommand

// endregion
