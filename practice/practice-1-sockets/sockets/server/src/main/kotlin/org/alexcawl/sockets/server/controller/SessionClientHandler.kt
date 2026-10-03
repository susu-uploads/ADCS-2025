package org.alexcawl.sockets.server.controller

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.sendMessage
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.server.TcpServer
import org.alexcawl.sockets.common.warning
import org.alexcawl.sockets.server.domain.session.OnReceiveMessageEvent
import org.alexcawl.sockets.server.domain.session.OnSocketDisconnectEvent
import org.alexcawl.sockets.server.domain.session.SendMessageNews
import org.alexcawl.sockets.server.domain.session.SessionNews
import org.alexcawl.sockets.server.domain.session.SessionStore
import kotlin.coroutines.cancellation.CancellationException

internal class SessionClientHandler(
    private val sessionStoreFactory: () -> SessionStore,
) : TcpServer.ClientHandler {
    override suspend fun handle(tcpClient: TcpClient) {
        coroutineScope {
            val sessionStore: SessionStore = sessionStoreFactory()
            val sessionStoreJob: Job = sessionStore.launchIn(coroutineScope = this)
            val readerJob = launch {
                tcpClient.receivedMessages.collect { message: TcpClient.Message ->
                    when (message) {
                        is TcpClient.Message.Content -> sessionStore.dispatch(event = OnReceiveMessageEvent(message = message.value))
                        is TcpClient.Message.Last -> sessionStore.dispatch(event = OnSocketDisconnectEvent)
                    }
                }
            }
            val writerJob = launch {
                sessionStore.news.collect { sessionNews: SessionNews ->
                    when (sessionNews) {
                        is SendMessageNews -> try {
                            tcpClient.sendMessage(payload = sessionNews.payload, lastAck = sessionNews.lastAck)
                        } catch (cancellationException: CancellationException) {
                            throw cancellationException
                        } catch (exception: Throwable) {
                            logger.warning(message = "Failed to send message", exception = exception)
                        }
                    }
                }
            }
            joinAll(readerJob, writerJob, sessionStoreJob)
        }
    }
}
