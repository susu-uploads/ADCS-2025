package org.alexcawl.sockets.server.testkit

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.TestScope
import org.alexcawl.sockets.common.Provider
import org.alexcawl.sockets.common.client.TcpClient
import org.alexcawl.sockets.common.client.TcpClient.Message
import org.alexcawl.sockets.common.client.sendMessage
import org.alexcawl.sockets.common.server.TcpServer
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.server.Application
import org.alexcawl.sockets.server.ApplicationConfiguration
import org.alexcawl.sockets.server.ApplicationModule
import org.alexcawl.sockets.server.controller.ControllerModule
import org.alexcawl.sockets.server.controller.TcpServerFactory
import org.alexcawl.sockets.server.domain.session.SessionStore
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

internal suspend fun TestScope.application(
    configuration: ApplicationConfiguration = applicationConfiguration(),
    serverFormat: Contract.ServerFormat = Contract.serverJsonFormat,
    block: suspend ApplicationTestScope.() -> Unit,
) {
    val applicationModule = TestApplicationModule(
        applicationConfiguration = configuration,
        serverFormat = serverFormat,
    )
    val application = TestApplication(applicationModule = applicationModule)
    ApplicationTestScopeImpl(
        parentScope = backgroundScope,
        application = application
    ).use { scope: ApplicationTestScope ->
        scope.block()
    }
}

// region Mocks

private class TestTcpServer(
    override val clientHandler: TcpServer.ClientHandler,
) : TcpServer {

    private val acceptedClients: Channel<TcpClient> = Channel(capacity = Channel.UNLIMITED)

    private val isLaunched: AtomicBoolean = AtomicBoolean(false)

    override fun launchIn(coroutineScope: CoroutineScope): Job {
        check(value = isLaunched.compareAndSet(false, true)) {
            "TestTcpServer is already launched"
        }
        return coroutineScope.launch {
            while (isActive) {
                val serverSideClient: TcpClient = acceptedClients.receive()
                launch {
                    serverSideClient.launchIn(coroutineScope = this)
                    clientHandler.handle(tcpClient = serverSideClient)
                }
            }
        }.also { serverJob: Job ->
            serverJob.invokeOnCompletion {
                acceptedClients.close()
            }
        }
    }

    suspend fun connectClient(): TcpClient {
        val clientToServer: Channel<String> = Channel(capacity = Channel.BUFFERED)
        val serverToClient: Channel<String> = Channel(capacity = Channel.BUFFERED)

        val serverSideClient: TcpClient = TestTcpClient(
            inboundTransport = clientToServer,
            outboundTransport = serverToClient,
        )
        val clientSideClient: TcpClient = TestTcpClient(
            inboundTransport = serverToClient,
            outboundTransport = clientToServer,
        )

        acceptedClients.send(element = serverSideClient)
        return clientSideClient
    }
}

private class TestTcpClient(
    private val inboundTransport: Channel<String>,
    private val outboundTransport: Channel<String>,
) : TcpClient {

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
            "TestTcpClient is already launched"
        }
        return coroutineScope.launch {
            val clientScope: CoroutineScope = this
            val readerJob: Job = launch {
                try {
                    for (message: String in inboundTransport) {
                        inboundMessages.send(element = message.asMessage())
                    }
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } finally {
                    inboundMessages.send(element = Message.Last)
                }
            }
            val writerJob: Job = launch {
                try {
                    for (message: Message in outboundMessages) {
                        when (message) {
                            is Message.Content -> outboundTransport.send(element = message.value)
                            is Message.Last -> clientScope.cancel(message = "Client closed")
                        }
                    }
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                }
            }
            joinAll(readerJob, writerJob)
        }.also { clientJob: Job ->
            clientJob.invokeOnCompletion {
                inboundMessages.close()
                outboundMessages.close()
                outboundTransport.close()
            }
        }
    }

    override val receivedMessages: Flow<Message> = inboundMessages.receiveAsFlow()

    override suspend fun sendMessage(message: Message) {
        outboundMessages.send(element = message)
    }

    private fun String.asMessage(): Message = Message.Content(value = this)
}

// endregion

// region DI

private class TestApplicationModule(
    applicationConfiguration: ApplicationConfiguration,
    serverFormat: Contract.ServerFormat,
) : ApplicationModule(
    applicationConfiguration = applicationConfiguration,
    serverFormat = serverFormat,
) {
    override val controllerModule: TestControllerModule by single {
        TestControllerModule(
            port = applicationConfiguration.port,
            acceptTimeoutMs = applicationConfiguration.acceptTimeoutMs,
            sessionStore = domainModule.sessionStore,
        )
    }
}

private class TestControllerModule(
    port: Int,
    acceptTimeoutMs: Int,
    sessionStore: Provider<SessionStore>,
) : ControllerModule(
    port = port,
    acceptTimeoutMs = acceptTimeoutMs,
    sessionStore = sessionStore,
) {
    val testTcpServer: TestTcpServer by single {
        TestTcpServer(clientHandler = clientHandler)
    }

    override val tcpServerFactory: TcpServerFactory by single {
        TcpServerFactory { testTcpServer }
    }
}

// endregion

// region Scopes

private class TestApplication(applicationModule: TestApplicationModule) : Application(applicationModule) {
    val testTcpServer: TestTcpServer = applicationModule.controllerModule.testTcpServer
}

private class ApplicationTestScopeImpl(
    parentScope: CoroutineScope,
    private val application: TestApplication,
) : ApplicationTestScope, AutoCloseable {

    private val applicationScope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + SupervisorJob())

    init {
        applicationScope.launch {
            application.launchIn(coroutineScope = this)
        }
    }

    override suspend fun TestScope.client(
        clientFormat: Contract.ClientFormat,
        block: suspend ClientTestScope.() -> Unit,
    ) {
        val tcpClient: TcpClient = application.testTcpServer.connectClient()
        TestClientScopeImpl(
            parentScope = backgroundScope,
            tcpClient = tcpClient,
            clientFormat = clientFormat,
        ).use { client: ClientTestScope ->
            block(client)
        }
    }

    override fun close() {
        applicationScope.cancel("Application test scope completed")
    }
}

private class TestClientScopeImpl(
    parentScope: CoroutineScope,
    private val tcpClient: TcpClient,
    private val clientFormat: Contract.ClientFormat,
) : ClientTestScope, AutoCloseable {

    private val outboundRequests: Channel<ClientRequest> = Channel(capacity = Channel.UNLIMITED)

    private val inboundResponses: MutableSharedFlow<ServerResponse> = MutableSharedFlow(replay = Int.MAX_VALUE)

    private val clientScope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + SupervisorJob())

    init {
        clientScope.launch {
            val tcpClientJob: Job = tcpClient.launchIn(coroutineScope = this)
            val readerJob: Job = launch {
                tcpClient.receivedMessages.collect { message: Message ->
                    when (message) {
                        is Message.Content -> {
                            val response: ServerResponse = clientFormat.decode(string = message.value)
                            inboundResponses.emit(value = response)
                        }
                        is Message.Last -> close()
                    }
                }
            }
            val writerJob: Job = launch {
                outboundRequests.receiveAsFlow().collect { request: ClientRequest ->
                    val payload: String = clientFormat.encode(value = request)
                    tcpClient.sendMessage(payload = payload)
                }
            }
            joinAll(tcpClientJob, readerJob, writerJob)
        }
    }

    override suspend fun sendRequest(request: ClientRequest) {
        outboundRequests.send(element = request)
    }

    override suspend fun sendRequest(vararg requests: ClientRequest) {
        requests.forEach { request: ClientRequest ->
            sendRequest(request = request)
        }
    }

    override val responses: ReceiveChannel<ServerResponse>
        get() = inboundResponses.produceIn(scope = clientScope)

    override suspend fun <SR : ServerResponse> receiveResponse(type: KClass<SR>, condition: (response: SR) -> Boolean): SR {
        return inboundResponses
            .filterIsInstance(klass = type)
            .filter(predicate = condition)
            .first()
    }

    override suspend fun <SR : ServerResponse> receiveResponse(type: KClass<SR>): SR {
        return inboundResponses
            .filterIsInstance(klass = type)
            .first()
    }

    override fun close() {
        clientScope.cancel("Client test scope completed")
        outboundRequests.close()
    }
}

// endregion
