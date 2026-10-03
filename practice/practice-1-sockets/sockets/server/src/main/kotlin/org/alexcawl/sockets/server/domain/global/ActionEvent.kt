package org.alexcawl.sockets.server.domain.global

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User

internal sealed interface ActionEvent : GlobalEvent


internal data class ChatCreateEvent(
    val chat: Chat,
    val timestamp: Long,
) : ActionEvent

internal data class ChatJoinEvent(
    val user: User,
    val chat: Chat,
    val timestamp: Long,
) : ActionEvent

internal data class ChatLeaveEvent(
    val user: User,
    val chat: Chat,
    val timestamp: Long,
) : ActionEvent

internal data class MessageSendEvent(
    val chat: Chat,
) : ActionEvent

internal data class UserAuthorizeEvent(
    val user: User,
    val timestamp: Long,
) : ActionEvent

internal data class UserChangeNameEvent(
    val user: User,
    val oldUser: User?,
    val timestamp: Long,
) : ActionEvent

internal data class UserDeauthorizeEvent(
    val user: User,
    val timestamp: Long,
) : ActionEvent
