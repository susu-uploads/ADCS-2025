package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.MessageSendCommand
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMessagesEvent

internal class MessageSendGlobalCommandsFlowHandler : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<MessageSendCommand>().transform { command: MessageSendCommand ->
            emit(value = OnUpdateChatMessagesEvent(chatId = command.chat.id))
        }
    }
}
