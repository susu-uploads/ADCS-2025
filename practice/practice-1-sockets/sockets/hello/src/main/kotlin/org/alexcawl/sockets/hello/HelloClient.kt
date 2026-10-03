package org.alexcawl.sockets.hello

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.receivedContent
import org.alexcawl.sockets.common.client.sendMessage
import org.alexcawl.sockets.common.decode
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.resourcesProperties
import java.net.Socket

fun main(): Unit = runBlocking {
    val configuration: HelloClientConfiguration = resourcesProperties(name = "hello.properties").decode()
    Socket(configuration.host, configuration.port).use { socket: Socket ->
        val tcpClient: TcpClient = TcpClient.create(socket = socket)
        supervisorScope {
            withContext(Dispatchers.IO) {
                val clientJob: Job = tcpClient.launchIn(coroutineScope = this)
                tcpClient.sendMessage(payload = "World")
                val response: String = tcpClient.receivedContent.first()
                logger.info("Server says: $response")
                clientJob.cancel()
            }
        }
    }
}

@Serializable
private data class HelloClientConfiguration(
    @SerialName(value = "hello.host")
    val host: String = "localhost",
    @SerialName(value = "hello.port")
    val port: Int = 8080,
    @SerialName(value = "hello.connectionTimeoutMs")
    val connectTimeoutMs: Int = 3000,
)
