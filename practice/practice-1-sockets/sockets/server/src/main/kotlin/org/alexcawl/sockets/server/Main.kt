package org.alexcawl.sockets.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.alexcawl.sockets.common.decode
import org.alexcawl.sockets.common.resourcesProperties
import org.alexcawl.sockets.contract.Contract

public fun main(): Unit = runBlocking {
    val applicationConfiguration: ApplicationConfiguration = resourcesProperties(name = "server.properties").decode()
    val applicationModule = ApplicationModule(
        applicationConfiguration = applicationConfiguration,
        serverFormat = Contract.serverJsonFormat,
    )
    supervisorScope {
        withContext(Dispatchers.IO) {
            Application(applicationModule = applicationModule).launchIn(coroutineScope = this)
        }
    }
}
