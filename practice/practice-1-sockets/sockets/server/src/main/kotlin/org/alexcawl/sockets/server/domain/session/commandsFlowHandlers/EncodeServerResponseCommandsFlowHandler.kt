package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.server.domain.session.EncodeServerResponseCommand
import org.alexcawl.sockets.server.domain.session.OnEncodeServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent

internal class EncodeServerResponseCommandsFlowHandler(
    private val serverFormat: Contract.ServerFormat,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<EncodeServerResponseCommand>()
            .map { command: EncodeServerResponseCommand ->
                OnEncodeServerResponseEvent(
                    payload = encodeServerResponse(response = command.response),
                    lastAck = command.lastAck,
                )
            }
    }

    private fun encodeServerResponse(response: ServerResponse): Result<String> {
        return runCatching {
            serverFormat.encode(value = response)
        }
    }
}