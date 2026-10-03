package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnNetworkFailureEvent
import org.alexcawl.sockets.client.domain.main.SendNetworkMessageCommand
import org.alexcawl.sockets.client.network.TcpNetwork
import org.alexcawl.sockets.client.network.sendMessage
import kotlin.coroutines.cancellation.CancellationException

internal class SendNetworkMessageCommandsFlowHandler(
    private val network: TcpNetwork,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<SendNetworkMessageCommand>()
            .transform { command: SendNetworkMessageCommand ->
                try {
                    network.sendMessage(payload = command.payload, lastAck = command.lastAck)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (throwable: Throwable) {
                    emit(OnNetworkFailureEvent(cause = throwable))
                }
            }
    }
}
