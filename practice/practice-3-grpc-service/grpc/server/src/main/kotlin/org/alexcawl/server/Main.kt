package org.alexcawl.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.alexcawl.common.decode
import org.alexcawl.common.resourcesProperties

public fun main(): Unit = runBlocking {
    val applicationConfiguration: ApplicationConfiguration = resourcesProperties(name = "server.properties").decode()
    val applicationModule = ApplicationModule(applicationConfiguration = applicationConfiguration)
    supervisorScope {
        withContext(Dispatchers.IO) {
            Application(applicationModule = applicationModule).launchIn(coroutineScope = this)
        }
    }
}
