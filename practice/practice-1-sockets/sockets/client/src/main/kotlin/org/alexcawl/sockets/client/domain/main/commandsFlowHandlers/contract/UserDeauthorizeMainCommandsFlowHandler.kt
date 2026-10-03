package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnReceiveClientRequestEvent
import org.alexcawl.sockets.client.domain.main.UserDeauthorizeCommand
import org.alexcawl.sockets.contract.request.UserDeauthorizeRequest

internal class UserDeauthorizeMainCommandsFlowHandler : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<UserDeauthorizeCommand>()
            .map { _: UserDeauthorizeCommand ->
                OnReceiveClientRequestEvent(request = UserDeauthorizeRequest, lastAck = true)
            }
    }
}
