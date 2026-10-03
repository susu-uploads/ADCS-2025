package org.alexcawl.sockets.client.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.TcpClient.Message
import java.net.InetSocketAddress
import java.net.Socket

internal class TcpNetworkImpl(
    private val coroutineScope: CoroutineScope,
    private val timeoutMs: Int,
) : TcpNetwork {

    private val mutex: Mutex = Mutex()

    private var tcpClient: TcpClient? = null
    private var tcpClientJob: Job? = null

    private val inboundMessages: MutableSharedFlow<Message> = MutableSharedFlow()

    override val receivedMessages: Flow<Message> = inboundMessages.asSharedFlow()

    override suspend fun sendMessage(message: Message) {
        val activeTcpClient: TcpClient = mutex.withLock {
            requireNotNull(value = tcpClient) {
                "Network is not connected"
            }
        }
        activeTcpClient.sendMessage(message = message)
    }

    override suspend fun connect(host: String, port: Int) {
        mutex.withLock {
            clear()
            val newSocket: Socket = createSocket(host = host, port = port)
            val newTcpClient: TcpClient = TcpClient.create(socket = newSocket)
            val newTcpClientJob: Job = coroutineScope.launch {
                newTcpClient.launchIn(coroutineScope = this)
                launch {
                    newTcpClient.receivedMessages.collect { message: Message ->
                        inboundMessages.emit(value = message)
                    }
                }
            }.also { job: Job ->
                job.invokeOnCompletion {
                    clear()
                }
            }
            tcpClient = newTcpClient
            tcpClientJob = newTcpClientJob
        }
    }

    override suspend fun disconnect() {
        mutex.withLock {
            clear()
        }
    }

    private fun clear() {
        tcpClientJob?.cancel()
        tcpClientJob = null
        tcpClient = null
    }

    private fun createSocket(host: String, port: Int): Socket {
        val socket = Socket()
        val endpoint = InetSocketAddress(host, port)
        socket.connect(endpoint, timeoutMs)
        return socket
    }
}
