package org.alexcawl.sockets.server.domain.global

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User

internal sealed interface GlobalEvent


internal data class OnCreatedSystemUserEvent(val systemUser: User) : GlobalEvent

internal data class OnCreatedSystemChatEvent(val systemChat: Chat) : GlobalEvent


internal data object OnUpdateChatsEvent : GlobalEvent

internal data object OnUpdateUsersEvent : GlobalEvent

internal data class OnUpdateChatMessagesEvent(val chatId: Int) : GlobalEvent

internal data class OnUpdateChatMembersEvent(val chatId: Int) : GlobalEvent
