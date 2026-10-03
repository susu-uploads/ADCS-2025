package org.alexcawl.sockets.client.domain.main.commandsFlowHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.MainCommand
import org.alexcawl.sockets.client.domain.main.MainEvent
import org.alexcawl.sockets.client.domain.main.OnNetworkDisconnectedEvent
import org.alexcawl.sockets.client.domain.main.OnReceiveMessageEvent
import org.alexcawl.sockets.client.domain.main.SubscribeOnNetworkMessagesCommand
import org.alexcawl.sockets.client.network.TcpNetwork
import org.alexcawl.sockets.common.client.TcpClient.Message

internal class SubscribeOnNetworkMessagesCommandsFlowHandler(
    private val network: TcpNetwork,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands.filterIsInstance<SubscribeOnNetworkMessagesCommand>()
            .flatMapLatest { _: SubscribeOnNetworkMessagesCommand ->
                network.receivedMessages.map { message: Message ->
                    when (message) {
                        is Message.Content -> OnReceiveMessageEvent(payload = message.value)
                        is Message.Last -> OnNetworkDisconnectedEvent
                    }
                }
            }
    }
}
