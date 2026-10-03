package org.alexcawl.sockets.common.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.alexcawl.sockets.common.client.TcpClient.Message
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException

internal class TcpClientImpl(private val socket: Socket) : TcpClient {

    private val inboundMessages: Channel<Message> = Channel(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val outboundMessages: Channel<Message> = Channel(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val isLaunched = AtomicBoolean(false)

    override fun launchIn(coroutineScope: CoroutineScope): Job {
        check(value = isLaunched.compareAndSet(false, true)) {
            "TcpClient is already launched"
        }
        return coroutineScope.launch {
            val clientJob: CoroutineScope = this
            socket.use { socket: Socket ->
                val readerJob: Job = launch {
                    reader(clientSocket = socket) { bufferedReader: BufferedReader ->
                        try {
                            while (isActive) {
                                val payload: String = bufferedReader.readLine() ?: break
                                inboundMessages.send(element = payload.asMessage())
                            }
                        } catch (cancellationException: CancellationException) {
                            throw cancellationException
                        } catch (_: IOException) {
                            // It's normal when it's closing
                        } finally {
                            inboundMessages.send(element = Message.Last)
                        }
                    }
                }
                val writerJob: Job = launch {
                    writer(clientSocket = socket) { writer: BufferedWriter ->
                        try {
                            for (message: Message in outboundMessages) {
                                when (message) {
                                    is Message.Content -> {
                                        writer.write(message.value)
                                        writer.newLine()
                                        writer.flush()
                                    }
                                    is Message.Last -> {
                                        clientJob.cancel(message = "Client closed")
                                    }
                                }
                            }
                        } catch (cancellationException: CancellationException) {
                            throw cancellationException
                        } catch (_: IOException) {
                            // It's normal when it's closing
                        }
                    }
                }
                joinAll(readerJob, writerJob)
            }
        }.also { clientJob: Job ->
            clientJob.invokeOnCompletion {
                inboundMessages.runCatching(block = Channel<Message>::close)
                outboundMessages.runCatching(block = Channel<Message>::close)
                socket.runCatching(block = Socket::close)
            }
        }
    }

    override val receivedMessages: Flow<Message> = inboundMessages.receiveAsFlow()

    override suspend fun sendMessage(message: Message) {
        outboundMessages.send(element = message)
    }

    private inline fun reader(clientSocket: Socket, block: (reader: BufferedReader) -> Unit) {
        val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream(), Charsets.UTF_8))
        return reader.use(block = block)
    }

    private inline fun writer(clientSocket: Socket, block: (writer: BufferedWriter) -> Unit) {
        val writer = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream(), Charsets.UTF_8))
        return writer.use(block = block)
    }

    private fun String.asMessage(): Message = Message.Content(value = this)
}
