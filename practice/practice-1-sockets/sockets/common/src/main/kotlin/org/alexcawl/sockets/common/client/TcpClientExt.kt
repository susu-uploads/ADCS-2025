package org.alexcawl.sockets.common.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import java.net.Socket

public fun TcpClient(socket: Socket): TcpClient = TcpClientImpl(socket = socket)

public suspend fun TcpClient.sendMessage(payload: String, lastAck: Boolean = false) {
    sendContent(content = payload)
    if (lastAck) {
        sendLastAck()
    }
}

public suspend fun TcpClient.sendContent(content: String) {
    sendMessage(message = TcpClient.Message.Content(value = content))
}

public suspend fun TcpClient.sendLastAck() {
    sendMessage(message = TcpClient.Message.Last)
}

public val TcpClient.receivedContent: Flow<String>
    get() = receivedMessages.filterIsContent()

public fun Flow<TcpClient.Message>.filterIsContent(): Flow<String> {
    return filterIsInstance<TcpClient.Message.Content>()
        .map { content: TcpClient.Message.Content -> content.value }
}
