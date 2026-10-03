package org.alexcawl.sockets.server.domain.session

import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.contract.response.ServerResponse
import java.util.UUID

internal sealed interface SessionEvent

internal data class OnDecodeClientRequestEvent(val request: Result<ClientRequest>) : SessionEvent

internal data class OnReceiveServerResponseEvent(val response: ServerResponse, val lastAck: Boolean = false) : SessionEvent

internal data class OnEncodeServerResponseEvent(val payload: Result<String>, val lastAck: Boolean = false) : SessionEvent

internal data class OnAuthorizeSessionEvent(val userId: UUID) : SessionEvent

internal data object OnDeauthorizeSessionEvent : SessionEvent

internal data object OnUpdateChatsEvent : SessionEvent

internal data object OnUpdateUsersEvent : SessionEvent

internal data class OnUpdateChatMessagesEvent(val chatId: Int) : SessionEvent

internal data class OnUpdateChatMembersEvent(val chatId: Int) : SessionEvent
