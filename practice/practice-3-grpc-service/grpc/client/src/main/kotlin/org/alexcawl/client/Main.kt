package org.alexcawl.client

import kotlinx.coroutines.Dispatchers
import org.alexcawl.common.decode
import org.alexcawl.common.resourcesProperties

public fun main() {
    val applicationConfiguration: ApplicationConfiguration = resourcesProperties(name = "client.properties").decode()
    val applicationModule = ApplicationModule(
        applicationConfiguration = applicationConfiguration,
        storeContext = Dispatchers.IO,
    )
    Application(applicationModule = applicationModule).launch()
}
