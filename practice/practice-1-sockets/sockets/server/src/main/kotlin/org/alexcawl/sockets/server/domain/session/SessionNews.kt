package org.alexcawl.sockets.server.domain.session

internal sealed interface SessionNews

internal data class SendMessageNews(val payload: String, val lastAck: Boolean = false) : SessionNews
