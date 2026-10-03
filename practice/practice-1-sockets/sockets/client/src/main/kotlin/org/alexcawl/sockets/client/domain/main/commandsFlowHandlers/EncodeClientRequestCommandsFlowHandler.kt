package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.EncodeClientRequestCommand
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnEncodeClientRequestEvent
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.contract.request.ClientRequest

internal class EncodeClientRequestCommandsFlowHandler(
    private val clientFormat: Contract.ClientFormat,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<EncodeClientRequestCommand>()
            .map { command: EncodeClientRequestCommand ->
                OnEncodeClientRequestEvent(payload = encodeClientRequest(request = command.request), lastAck = command.lastAck)
            }
    }

    private fun encodeClientRequest(request: ClientRequest): Result<String> {
        return runCatching {
            clientFormat.encode(value = request)
        }
    }
}
