package org.alexcawl.sockets.client

import kotlinx.coroutines.CoroutineScope
import org.alexcawl.sockets.client.data.DataModule
import org.alexcawl.sockets.client.domain.DomainModule
import org.alexcawl.sockets.client.network.NetworkModule
import org.alexcawl.sockets.client.ui.UiModule
import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.contract.Contract
import kotlin.coroutines.CoroutineContext

internal class ApplicationModule(
    private val applicationConfiguration: ApplicationConfiguration,
    private val clientFormat: Contract.ClientFormat,
    private val networkScope: CoroutineScope,
    private val storeContext: CoroutineContext,
) : Container {

    val dataModule: DataModule by single {
        DataModule()
    }

    val networkModule: NetworkModule by single {
        NetworkModule(
            networkScope = networkScope,
            timeoutMs = applicationConfiguration.timeoutMs,
        )
    }

    val domainModule: DomainModule by single {
        DomainModule(
            isVerbose = applicationConfiguration.isVerbose,
            defaultHost = applicationConfiguration.defaultHost,
            defaultPort = applicationConfiguration.defaultPort,
            network = networkModule.network,
            clientFormat = clientFormat,
            userIdGenerator = dataModule.userIdGenerator,
        )
    }

    val uiModule: UiModule by single {
        UiModule(
            loginStore = domainModule::loginStore,
            mainStore = domainModule::mainStore,
            storeContext = storeContext,
        )
    }
}
