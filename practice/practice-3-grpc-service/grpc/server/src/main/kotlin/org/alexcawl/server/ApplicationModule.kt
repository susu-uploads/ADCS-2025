package org.alexcawl.server

import org.alexcawl.common.Container
import org.alexcawl.server.controller.ControllerModule
import org.alexcawl.server.data.DataModule
import org.alexcawl.server.domain.DomainModule

internal open class ApplicationModule(
    private val applicationConfiguration: ApplicationConfiguration,
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
            database = dataModule.database,
        )
    }

    open val controllerModule: ControllerModule by single {
        ControllerModule(
            port = applicationConfiguration.port,
            authorizeUserUseCase = domainModule.authorizeUserUseCase,
            createPostUseCase = domainModule.createPostUseCase,
            listFeedUseCase = domainModule.listFeedUseCase,
            likePostUseCase = domainModule.likePostUseCase,
            dislikePostUseCase = domainModule.dislikePostUseCase,
            commentPostUseCase = domainModule.commentPostUseCase,
        )
    }
}
