package org.alexcawl.sockets.server.controller

import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.common.Provider
import org.alexcawl.sockets.common.server.TcpServer
import org.alexcawl.sockets.server.domain.session.SessionStore
import java.net.ServerSocket

internal open class ControllerModule(
    private val port: Int,
    private val acceptTimeoutMs: Int,
    private val sessionStore: Provider<SessionStore>,
) : Container {

    open val tcpServerFactory: TcpServerFactory by single {
        TcpServerFactory {
            val serverSocket: ServerSocket = ServerSocket(port).apply {
                soTimeout = acceptTimeoutMs
            }
            TcpServer.create(serverSocket = serverSocket, clientHandler = clientHandler)
        }
    }

    val clientHandler: SessionClientHandler by single {
        SessionClientHandler(sessionStoreFactory = sessionStore::invoke)
    }
}
