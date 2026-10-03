package org.alexcawl.sockets.server.domain.global

import org.alexcawl.kotea.DslUpdate

internal class GlobalUpdate : DslUpdate<GlobalState, GlobalEvent, GlobalCommand, GlobalIntent>() {

    override fun NextBuilder.update(event: GlobalEvent) {
        when (event) {
            // External events
            is ActionEvent -> onSystem(event = event)
            // Internal events
            is OnCreatedSystemUserEvent -> onCreatedSystemUser(event = event)
            is OnCreatedSystemChatEvent -> onCreatedSystemChat(event = event)
            is OnUpdateChatMessagesEvent -> onUpdateChatMessages(event = event)
            is OnUpdateChatMembersEvent -> onUpdateChatMembers(event = event)
            is OnUpdateChatsEvent -> onUpdateChats(event = event)
            is OnUpdateUsersEvent -> onUpdateUsers(event = event)
        }
    }

    private fun NextBuilder.onSystem(event: ActionEvent) {
        commands(
            when (event) {
                is ChatCreateEvent -> ChatCreateCommand(
                    systemUserId = state.systemUserId,
                    chat = event.chat,
                    timestamp = event.timestamp,
                )

                is ChatJoinEvent -> ChatJoinCommand(
                    systemUserId = state.systemUserId,
                    user = event.user,
                    chat = event.chat,
                    timestamp = event.timestamp,
                )

                is ChatLeaveEvent -> ChatLeaveCommand(
                    systemUserId = state.systemUserId,
                    user = event.user,
                    chat = event.chat,
                    timestamp = event.timestamp,
                )

                is MessageSendEvent -> MessageSendCommand(
                    chat = event.chat,
                )

                is UserAuthorizeEvent -> UserAuthorizeCommand(
                    systemUserId = state.systemUserId,
                    systemChatId = state.systemChatId,
                    user = event.user,
                    timestamp = event.timestamp,
                )

                is UserChangeNameEvent -> UserChangeNameCommand(
                    systemUserId = state.systemUserId,
                    systemChatId = state.systemChatId,
                    oldUserName = event.oldUser?.name,
                    newUserName = event.user.name,
                    timestamp = event.timestamp,
                )

                is UserDeauthorizeEvent -> UserDeauthorizeCommand(
                    systemUserId = state.systemUserId,
                    systemChatId = state.systemChatId,
                    user = event.user,
                    timestamp = event.timestamp,
                )
            }
        )
    }

    private fun NextBuilder.onCreatedSystemUser(event: OnCreatedSystemUserEvent) {
        state {
            copy(systemUserId = event.systemUser.id)
        }
        commands(
            CreateSystemChatCommand(systemUserId = event.systemUser.id)
        )
    }

    private fun NextBuilder.onCreatedSystemChat(event: OnCreatedSystemChatEvent) {
        state {
            copy(systemChatId = event.systemChat.id)
        }
    }

    private fun NextBuilder.onUpdateChatMessages(event: OnUpdateChatMessagesEvent) {
        news(UpdateChatMessagesIntent(chatId = event.chatId))
    }

    private fun NextBuilder.onUpdateChatMembers(event: OnUpdateChatMembersEvent) {
        news(UpdateChatMembersIntent(chatId = event.chatId))
    }

    private fun NextBuilder.onUpdateChats(event: OnUpdateChatsEvent) {
        news(UpdateChatsIntent)
    }

    private fun NextBuilder.onUpdateUsers(event: OnUpdateUsersEvent) {
        news(UpdateUsersIntent)
    }
}
