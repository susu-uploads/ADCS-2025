package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.DisconnectNetworkCommand
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnNetworkFailureEvent
import org.alexcawl.sockets.client.network.TcpNetwork
import kotlin.coroutines.cancellation.CancellationException

internal class DisconnectNetworkCommandsFlowHandler(
    private val network: TcpNetwork,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<DisconnectNetworkCommand>()
            .transform { _: DisconnectNetworkCommand ->
                try {
                    network.disconnect()

                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (throwable: Throwable) {
                    emit(OnNetworkFailureEvent(cause = throwable))
                }
            }
    }
}
