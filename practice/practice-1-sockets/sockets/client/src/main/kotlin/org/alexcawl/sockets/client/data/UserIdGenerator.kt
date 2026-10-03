package org.alexcawl.sockets.client.data

import java.util.UUID

internal fun interface UserIdGenerator {
    fun generateUserId(): UUID
}
