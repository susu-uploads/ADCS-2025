package org.alexcawl.sockets.server.domain.global

internal sealed interface GlobalIntent


internal data object UpdateChatsIntent : GlobalIntent

internal data object UpdateUsersIntent : GlobalIntent

internal data class UpdateChatMessagesIntent(val chatId: Int) : GlobalIntent

internal data class UpdateChatMembersIntent(val chatId: Int) : GlobalIntent
