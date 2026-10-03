package org.alexcawl.client.domain.login

internal data class LoginState(
    val host: String,
    val port: String,
    val userName: String,
)
