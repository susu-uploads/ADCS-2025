package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.DecodeServerResponseCommand
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnDecodeServerResponseEvent
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.contract.response.ServerResponse

internal class DecodeServerResponseCommandsFlowHandler(
    private val clientFormat: Contract.ClientFormat,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<DecodeServerResponseCommand>()
            .map { command: DecodeServerResponseCommand ->
                OnDecodeServerResponseEvent(request = decodeServerResponse(string = command.payload))
            }
    }

    private fun decodeServerResponse(string: String): Result<ServerResponse> {
        return runCatching {
            clientFormat.decode(string = string)
        }
    }
}
