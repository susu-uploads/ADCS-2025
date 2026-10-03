package org.alexcawl.sockets.server.domain.session

import java.util.UUID

internal sealed interface SessionState {

    data object NotAuthorized : SessionState

    data class Authorized(val userId: UUID) : SessionState
}
