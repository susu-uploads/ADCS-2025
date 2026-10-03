package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnReceiveClientRequestEvent
import org.alexcawl.sockets.client.domain.main.UserChangeNameCommand
import org.alexcawl.sockets.contract.request.UserChangeNameRequest

internal class UserChangeNameMainCommandsFlowHandler : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<UserChangeNameCommand>()
            .map { command: UserChangeNameCommand ->
                OnReceiveClientRequestEvent(
                    request = UserChangeNameRequest(userName = command.userName),
                )
            }
    }
}
