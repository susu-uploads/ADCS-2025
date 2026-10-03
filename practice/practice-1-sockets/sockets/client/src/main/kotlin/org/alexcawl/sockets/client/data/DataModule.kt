package org.alexcawl.sockets.client.data

import org.alexcawl.sockets.common.Container

internal class DataModule : Container {

    val userIdGenerator: UserIdGenerator by single {
        UserIdGeneratorImpl()
    }
}
