package org.alexcawl.sockets.common.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.net.Socket

public interface TcpClient {

    public fun launchIn(coroutineScope: CoroutineScope): Job

    public val receivedMessages: Flow<Message>

    public suspend fun sendMessage(message: Message)

    public sealed interface Message {

        @JvmInline
        public value class Content(public val value: String) : Message

        public data object Last : Message
    }

    public companion object Factory {
        public fun create(socket: Socket): TcpClient {
            return TcpClientImpl(socket = socket)
        }
    }
}
