package org.alexcawl.server

import io.grpc.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.alexcawl.common.logger

internal open class Application(
    private val applicationModule: ApplicationModule,
) {

    protected val grpcServer: Server
        get() = applicationModule.controllerModule.grpcServer

    internal fun launchIn(coroutineScope: CoroutineScope): Job {
        applicationModule.dataModule.database
        return coroutineScope.launch {
            grpcServer.start()
            logger.info("gRPC server started on port ${grpcServer.port}.")
            grpcServer.awaitTermination()
        }
    }
}
