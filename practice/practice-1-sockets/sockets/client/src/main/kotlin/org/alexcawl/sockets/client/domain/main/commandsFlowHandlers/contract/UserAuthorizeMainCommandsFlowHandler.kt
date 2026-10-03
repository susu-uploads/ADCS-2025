package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.data.UserIdGenerator
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnReceiveClientRequestEvent
import org.alexcawl.sockets.client.domain.main.UserAuthorizeCommand
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import java.util.UUID

internal class UserAuthorizeMainCommandsFlowHandler(
    private val userIdGenerator: UserIdGenerator,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<UserAuthorizeCommand>()
            .map { command: UserAuthorizeCommand ->
                val userId: UUID = command.userId ?: userIdGenerator.generateUserId()
                OnReceiveClientRequestEvent(
                    request = UserAuthorizeRequest(
                        userId = userId,
                        userName = command.userName,
                    ),
                )
            }
    }
}
