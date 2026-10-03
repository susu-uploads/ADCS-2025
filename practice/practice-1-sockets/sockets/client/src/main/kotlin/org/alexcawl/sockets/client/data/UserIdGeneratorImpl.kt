package org.alexcawl.sockets.client.data

import org.alexcawl.sockets.common.logger
import java.util.UUID

internal class UserIdGeneratorImpl : UserIdGenerator {

    override fun generateUserId(): UUID {
        return UUID.randomUUID().also { userId: UUID ->
            logger.info("Generated user id: [$userId]")
        }
    }
}
