package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.ChatCreateCommand
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnReceiveClientRequestEvent
import org.alexcawl.sockets.contract.request.ChatCreateRequest

internal class ChatCreateMainCommandsFlowHandler : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<ChatCreateCommand>()
            .map { command: ChatCreateCommand ->
                OnReceiveClientRequestEvent(
                    request = ChatCreateRequest(chatName = command.chatName),
                )
            }
    }
}
