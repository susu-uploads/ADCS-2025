package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.MessageSendCommand
import org.alexcawl.sockets.client.domain.main.OnReceiveClientRequestEvent
import org.alexcawl.sockets.contract.request.MessageSendRequest

internal class MessageSendMainCommandsFlowHandler : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<MessageSendCommand>()
            .map { command: MessageSendCommand ->
                OnReceiveClientRequestEvent(
                    request = MessageSendRequest(
                        chatId = command.chatId,
                        message = command.message,
                        messageType = command.type,
                    ),
                )
            }
    }
}
