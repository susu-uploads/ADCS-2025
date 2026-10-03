package org.alexcawl.sockets.client.domain.login

internal sealed interface LoginCommand

internal data class EstablishConnection(val host: String, val port: Int) : LoginCommand
