package org.alexcawl.sockets.server.domain.session

import org.alexcawl.kotea.DslUpdate
import org.alexcawl.sockets.contract.request.*
import org.alexcawl.sockets.contract.response.FailureResponse
import org.alexcawl.sockets.contract.response.NotAuthorizedResponse

internal class SessionUpdate(
    private val serverIsEcho: Boolean,
) : DslUpdate<SessionState, SessionEvent, SessionCommand, SessionNews>() {

    override fun NextBuilder.update(event: SessionEvent) {
        when (event) {
            // External event
            is SocketEvent -> onSocketEvent(event = event)
            // Internal event
            is OnDecodeClientRequestEvent -> onDecodeClientRequest(event = event)
            is OnReceiveServerResponseEvent -> onReceiveServerResponse(event = event)
            is OnEncodeServerResponseEvent -> onEncodeServerResponse(event = event)
            is OnAuthorizeSessionEvent -> onAuthorizeSession(event = event)
            is OnDeauthorizeSessionEvent -> onDeauthorizeSession()
            is OnUpdateChatMessagesEvent -> onUpdateChatMessagesEvent(event = event)
            is OnUpdateChatMembersEvent -> onUpdateChatMembersEvent(event = event)
            is OnUpdateChatsEvent -> onUpdateChatsEvent()
            is OnUpdateUsersEvent -> onUpdateUsersEvent()
        }
    }

    private fun NextBuilder.onSocketEvent(event: SocketEvent) {
        when (event) {
            is OnReceiveMessageEvent -> {
                if (serverIsEcho) {
                    news(SendMessageNews(payload = event.message))
                } else {
                    commands(DecodeClientRequestCommand(payload = event.message))
                }
            }
            is OnSocketDisconnectEvent -> {
                val sessionState: SessionState = state
                if (sessionState is SessionState.Authorized) {
                    commands(
                        UserDeauthorizeCommand(
                            userId = sessionState.userId,
                        )
                    )
                }
            }
        }
    }

    private fun NextBuilder.onDecodeClientRequest(event: OnDecodeClientRequestEvent) {
        event.request
            .onSuccess { request: ClientRequest ->
                when (val sessionState: SessionState = state) {
                    is SessionState.NotAuthorized -> {
                        if (request is UserAuthorizeRequest) {
                            commands(
                                UserAuthorizeCommand(
                                    userId = request.userId,
                                    userName = request.userName,
                                )
                            )
                        } else {
                            commands(EncodeServerResponseCommand(response = NotAuthorizedResponse))
                        }
                    }
                    is SessionState.Authorized -> {
                        when (request) {
                            is ChatCreateRequest -> commands(
                                ChatCreateCommand(
                                    userId = sessionState.userId,
                                    chatName = request.chatName,
                                )
                            )

                            is ChatJoinRequest -> commands(
                                ChatJoinCommand(
                                    userId = sessionState.userId,
                                    chatId = request.chatId,
                                )
                            )

                            is ChatLeaveRequest -> commands(
                                ChatLeaveCommand(
                                    userId = sessionState.userId,
                                    chatId = request.chatId,
                                )
                            )

                            is MessageSendRequest -> commands(
                                MessageSendCommand(
                                    userId = sessionState.userId,
                                    chatId = request.chatId,
                                    message = request.message,
                                    type = request.messageType
                                )
                            )

                            is UserChangeNameRequest -> commands(
                                UserChangeNameCommand(
                                    userId = sessionState.userId,
                                    userName = request.userName,
                                )
                            )

                            is UserDeauthorizeRequest -> commands(
                                UserDeauthorizeCommand(
                                    userId = sessionState.userId,
                                )
                            )

                            is UserAuthorizeRequest -> commands(
                                UserAuthorizeCommand(
                                    userId = sessionState.userId,
                                    userName = null,
                                )
                            )
                        }
                    }
                }
            }
            .onFailure { exception: Throwable ->
                commands(EncodeServerResponseCommand(response = FailureResponse(message = exception.message.orEmpty())))
            }
    }

    private fun NextBuilder.onReceiveServerResponse(event: OnReceiveServerResponseEvent) {
        commands(EncodeServerResponseCommand(response = event.response, lastAck = event.lastAck))
    }

    private fun NextBuilder.onEncodeServerResponse(event: OnEncodeServerResponseEvent) {
        event.payload
            .onSuccess { payload ->
                news(SendMessageNews(payload = payload, lastAck = event.lastAck))
            }
            .onFailure { exception: Throwable ->
                commands(EncodeServerResponseCommand(response = FailureResponse(message = exception.message.orEmpty())))
            }
    }

    private fun NextBuilder.onAuthorizeSession(event: OnAuthorizeSessionEvent) {
        state {
            SessionState.Authorized(userId = event.userId)
        }
    }

    private fun NextBuilder.onDeauthorizeSession() {
        state {
            SessionState.NotAuthorized
        }
    }

    private fun NextBuilder.onUpdateChatMessagesEvent(event: OnUpdateChatMessagesEvent) {
        val sessionState: SessionState = state
        if (sessionState is SessionState.Authorized) {
            commands(UpdateChatMessagesCommand(userId = sessionState.userId, chatId = event.chatId))
        }
    }

    private fun NextBuilder.onUpdateChatMembersEvent(event: OnUpdateChatMembersEvent) {
        val sessionState: SessionState = state
        if (sessionState is SessionState.Authorized) {
            commands(UpdateChatMembersCommand(userId = sessionState.userId, chatId = event.chatId))
        }
    }

    private fun NextBuilder.onUpdateChatsEvent() {
        val sessionState: SessionState = state
        if (sessionState is SessionState.Authorized) {
            commands(UpdateChatsCommand)
        }
    }

    private fun NextBuilder.onUpdateUsersEvent() {
        val sessionState: SessionState = state
        if (sessionState is SessionState.Authorized) {
            commands(UpdateUsersCommand)
        }
    }
}
