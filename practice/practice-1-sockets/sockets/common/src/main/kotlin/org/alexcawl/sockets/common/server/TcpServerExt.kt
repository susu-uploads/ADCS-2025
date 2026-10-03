package org.alexcawl.sockets.common.server

import org.alexcawl.sockets.common.server.TcpServer.ClientHandler
import java.net.ServerSocket

public fun TcpServer(serverSocket: ServerSocket, clientHandler: ClientHandler): TcpServer {
    return TcpServerImpl(serverSocket = serverSocket, clientHandler = clientHandler)
}
