package org.alexcawl.sockets.server

import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.server.controller.ControllerModule
import org.alexcawl.sockets.server.data.DataModule
import org.alexcawl.sockets.server.domain.DomainModule

internal open class ApplicationModule(
    private val applicationConfiguration: ApplicationConfiguration,
    private val serverFormat: Contract.ServerFormat,
) : Container {

    val dataModule: DataModule by single {
        DataModule(
            databaseUrl = applicationConfiguration.databaseUrl,
            databaseDriver = applicationConfiguration.databaseDriver,
            databaseUser = applicationConfiguration.databaseUser,
            databasePassword = applicationConfiguration.databasePassword,
        )
    }

    val domainModule: DomainModule by single {
        DomainModule(
            isEcho = applicationConfiguration.isEcho,
            isVerbose = applicationConfiguration.isVerbose,
            systemUserName = applicationConfiguration.systemUserName,
            systemChatName = applicationConfiguration.systemChatName,
            serverFormat = serverFormat,
            database = dataModule.database,
        )
    }

    open val controllerModule: ControllerModule by single {
        ControllerModule(
            port = applicationConfiguration.port,
            acceptTimeoutMs = applicationConfiguration.acceptTimeoutMs,
            sessionStore = domainModule.sessionStore,
        )
    }
}
