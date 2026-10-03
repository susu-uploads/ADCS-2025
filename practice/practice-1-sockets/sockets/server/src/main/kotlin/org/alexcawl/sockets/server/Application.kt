package org.alexcawl.sockets.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.alexcawl.sockets.common.server.TcpServer
import org.alexcawl.sockets.server.controller.TcpServerFactory
import org.alexcawl.sockets.server.domain.global.GlobalStore

internal open class Application(
    private val applicationModule: ApplicationModule,
) {

    protected val tcpServerFactory: TcpServerFactory
        get() = applicationModule.controllerModule.tcpServerFactory

    protected val globalStore: GlobalStore
        get() = applicationModule.domainModule.globalStore

    internal fun launchIn(coroutineScope: CoroutineScope): Job {
        return coroutineScope.launch {
            val server: TcpServer = tcpServerFactory.create()
            val serverJob: Job = server.launchIn(coroutineScope = coroutineScope)
            val globalStoreJob: Job = globalStore.launchIn(coroutineScope = coroutineScope)
            joinAll(serverJob, globalStoreJob)
        }
    }
}
