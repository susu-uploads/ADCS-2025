package org.alexcawl.sockets.client.domain.login

internal data class LoginState(
    val host: String,
    val port: Int,
    val userName: String,
)
