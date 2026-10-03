package org.alexcawl.sockets.client.domain.login.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.login.EstablishConnection
import org.alexcawl.sockets.client.domain.login.LoginCommand
import org.alexcawl.sockets.client.domain.login.LoginEvent
import org.alexcawl.sockets.client.domain.login.OnConnectFailedEvent
import org.alexcawl.sockets.client.domain.login.OnConnectedEvent
import org.alexcawl.sockets.client.network.TcpNetwork
import kotlin.coroutines.cancellation.CancellationException

internal class EstablishConnectionCommandsFlowHandler(
    private val network: TcpNetwork,
) : CommandsFlowHandler<LoginCommand, LoginEvent> {

    override fun handle(commands: Flow<LoginCommand>): Flow<LoginEvent> {
        return commands.filterIsInstance<EstablishConnection>().map { command: EstablishConnection ->
            try {
                network.connect(host = command.host, port = command.port)
                OnConnectedEvent
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (throwable: Throwable) {
                OnConnectFailedEvent(cause = throwable)
            }
        }
    }
}
