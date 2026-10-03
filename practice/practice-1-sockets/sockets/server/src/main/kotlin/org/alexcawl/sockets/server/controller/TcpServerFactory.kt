package org.alexcawl.sockets.server.controller

import org.alexcawl.sockets.common.server.TcpServer

internal fun interface TcpServerFactory {

    fun create(): TcpServer
}
