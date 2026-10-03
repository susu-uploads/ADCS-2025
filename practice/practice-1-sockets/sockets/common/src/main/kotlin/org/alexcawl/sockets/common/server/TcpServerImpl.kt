package org.alexcawl.sockets.common.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.alexcawl.sockets.common.client.TcpClient
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicBoolean

internal class TcpServerImpl(
    private val serverSocket: ServerSocket,
    override val clientHandler: TcpServer.ClientHandler,
) : TcpServer {

    private val isLaunched = AtomicBoolean(false)

    override fun launchIn(coroutineScope: CoroutineScope): Job {
        check(value = isLaunched.compareAndSet(false, true)) {
            "TcpServer is already launched"
        }
        return coroutineScope.launch {
            serverSocket.use { serverSocket: ServerSocket ->
                while (isActive) {
                    try {
                        val clientSocket: Socket = serverSocket.accept()
                        val tcpClient = TcpClient(socket = clientSocket)
                        launch {
                            tcpClient.launchIn(coroutineScope = this)
                            clientHandler.handle(tcpClient = tcpClient)
                        }
                    } catch (_: SocketTimeoutException) {
                        continue
                    } catch (_: SocketException) {
                        // Typically thrown when serverSocket is closed.
                        break
                    }
                }
            }
        }.also { serverJob: Job ->
            serverJob.invokeOnCompletion {
                serverSocket.runCatching(block = ServerSocket::close)
            }
        }
    }
}