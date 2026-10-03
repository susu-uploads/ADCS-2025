package org.alexcawl.sockets.common.server

import kotlinx.coroutines.*
import org.alexcawl.sockets.common.client.TcpClient
import java.net.*

public interface TcpServer {

    public val clientHandler: ClientHandler

    public fun launchIn(coroutineScope: CoroutineScope): Job

    public fun interface ClientHandler {
        public suspend fun handle(tcpClient: TcpClient)
    }

    public companion object Factory {
        public fun create(serverSocket: ServerSocket, clientHandler: ClientHandler): TcpServer {
            return TcpServerImpl(serverSocket = serverSocket, clientHandler = clientHandler)
        }
    }
}
