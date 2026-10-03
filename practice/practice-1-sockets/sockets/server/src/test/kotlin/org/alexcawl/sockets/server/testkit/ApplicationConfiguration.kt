package org.alexcawl.sockets.server.testkit

import org.alexcawl.sockets.server.ApplicationConfiguration
import java.util.UUID

internal fun applicationConfiguration(): ApplicationConfiguration {
    val databaseName = "sockets-test-${UUID.randomUUID()}"
    return ApplicationConfiguration().copy(databaseUrl = "jdbc:h2:mem:$databaseName;DB_CLOSE_DELAY=-1;")
}
