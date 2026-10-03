package org.alexcawl.sockets.client.network

import kotlinx.coroutines.flow.Flow
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.filterIsContent

public suspend fun TcpNetwork.sendMessage(payload: String, lastAck: Boolean = false) {
    sendContent(content = payload)
    if (lastAck) {
        sendLastAck()
    }
}

public suspend fun TcpNetwork.sendContent(content: String) {
    sendMessage(message = TcpClient.Message.Content(value = content))
}

public suspend fun TcpNetwork.sendLastAck() {
    sendMessage(message = TcpClient.Message.Last)
}

public val TcpNetwork.receivedContent: Flow<String>
    get() = receivedMessages.filterIsContent()
