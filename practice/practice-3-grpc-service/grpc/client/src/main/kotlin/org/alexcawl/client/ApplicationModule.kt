package org.alexcawl.client

import org.alexcawl.client.domain.DomainModule
import org.alexcawl.client.network.NetworkModule
import org.alexcawl.client.ui.UiModule
import org.alexcawl.common.Container
import kotlin.coroutines.CoroutineContext

internal class ApplicationModule(
    private val applicationConfiguration: ApplicationConfiguration,
    private val storeContext: CoroutineContext,
) : Container {

    fun networkModule(host: String, port: Int): NetworkModule {
        return NetworkModule(host = host, port = port)
    }

    val domainModule: DomainModule by single {
        DomainModule(
            isVerbose = applicationConfiguration.isVerbose,
            defaultHost = applicationConfiguration.defaultHost,
            defaultPort = applicationConfiguration.defaultPort,
        )
    }

    val uiModule: UiModule by single {
        UiModule(
            loginStore = domainModule::loginStore,
            mainStore = domainModule::mainStore,
            networkModule = ::networkModule,
            storeContext = storeContext,
        )
    }
}
