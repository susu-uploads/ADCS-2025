package org.alexcawl.sockets.server.domain.session

internal sealed interface SocketEvent : SessionEvent


internal data class OnReceiveMessageEvent(val message: String) : SocketEvent

internal data object OnSocketDisconnectEvent : SocketEvent
