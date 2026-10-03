package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.server.domain.session.DecodeClientRequestCommand
import org.alexcawl.sockets.server.domain.session.OnDecodeClientRequestEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent

internal class DecodeClientRequestCommandsFlowHandler(
    private val serverFormat: Contract.ServerFormat,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<DecodeClientRequestCommand>()
            .map { command: DecodeClientRequestCommand ->
                OnDecodeClientRequestEvent(request = decodeClientRequest(string = command.payload))
            }
    }

    private fun decodeClientRequest(string: String): Result<ClientRequest> {
        return runCatching {
            serverFormat.decode(string = string)
        }
    }
}