package org.alexcawl.sockets.server.domain.global

import java.util.UUID

internal data class GlobalState(
    val systemUserId: UUID? = null,
    val systemChatId: Int? = null,
)
