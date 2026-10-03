package org.alexcawl.sockets.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.alexcawl.sockets.common.decode
import org.alexcawl.sockets.common.resourcesProperties
import org.alexcawl.sockets.contract.Contract

fun main() {
    val applicationConfiguration: ApplicationConfiguration = resourcesProperties(name = "client.properties").decode()
    val applicationModule = ApplicationModule(
        applicationConfiguration = applicationConfiguration,
        clientFormat = Contract.clientJsonFormat,
        storeContext = Dispatchers.IO,
        networkScope = CoroutineScope(context = Dispatchers.IO),
    )
    Application(applicationModule = applicationModule).launch()
}
