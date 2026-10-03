package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.GlobalIntent
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.UpdateChatMembersIntent
import org.alexcawl.sockets.server.domain.global.UpdateChatMessagesIntent
import org.alexcawl.sockets.server.domain.global.UpdateChatsIntent
import org.alexcawl.sockets.server.domain.global.UpdateUsersIntent
import org.alexcawl.sockets.server.domain.session.OnUpdateChatMembersEvent
import org.alexcawl.sockets.server.domain.session.OnUpdateChatMessagesEvent
import org.alexcawl.sockets.server.domain.session.OnUpdateChatsEvent
import org.alexcawl.sockets.server.domain.session.OnUpdateUsersEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.SubscribeOnGlobalUpdatesCommand

internal class SubscribeOnGlobalUpdatesCommandsFlowHandler(
    private val globalStore: GlobalStore,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<SubscribeOnGlobalUpdatesCommand>()
            .flatMapLatest { _: SubscribeOnGlobalUpdatesCommand ->
                globalStore.news.map { news: GlobalIntent ->
                    when (news) {
                        is UpdateChatMessagesIntent -> OnUpdateChatMessagesEvent(chatId = news.chatId)
                        is UpdateChatMembersIntent -> OnUpdateChatMembersEvent(chatId = news.chatId)
                        is UpdateChatsIntent -> OnUpdateChatsEvent
                        is UpdateUsersIntent -> OnUpdateUsersEvent
                    }
                }
            }
    }
}
