package org.alexcawl.sockets.hello

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.alexcawl.sockets.common.*
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.filterIsContent
import org.alexcawl.sockets.common.client.receivedContent
import org.alexcawl.sockets.common.client.sendContent
import org.alexcawl.sockets.common.client.sendMessage
import org.alexcawl.sockets.common.server.TcpServer
import java.net.InetSocketAddress
import java.net.ServerSocket

fun main(): Unit = runBlocking {
    val configuration: HelloServerConfiguration = resourcesProperties(name = "hello.properties").decode()
    ServerSocket().apply {
        bind(InetSocketAddress(configuration.port))
        soTimeout = configuration.acceptTimeoutMs
    }.use { serverSocket: ServerSocket ->
        val server: TcpServer = TcpServer.create(serverSocket = serverSocket) { tcpClient: TcpClient ->
            tcpClient.receivedContent.collect { message: String ->
                logger.info("Received message: $message")
                tcpClient.sendMessage(payload = "Hello, $message")
            }
        }
        supervisorScope {
            withContext(Dispatchers.IO) {
                val serverJob: Job = server.launchIn(coroutineScope = this)
                logger.info("Server started, listening on $server")
                serverJob.join()
            }
        }
    }
}

@Serializable
private data class HelloServerConfiguration(
    @SerialName(value = "hello.port")
    val port: Int = 8080,
    @SerialName(value = "hello.acceptTimeoutMs")
    val acceptTimeoutMs: Int = 3000,
)
