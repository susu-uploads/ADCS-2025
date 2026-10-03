package org.alexcawl.sockets.client.network

import kotlinx.coroutines.flow.Flow
import org.alexcawl.sockets.common.client.TcpClient.Message

public interface TcpNetwork {

    public val receivedMessages: Flow<Message>

    public suspend fun sendMessage(message: Message)

    public suspend fun connect(host: String, port: Int)

    public suspend fun disconnect()
}
