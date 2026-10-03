package org.alexcawl.sockets.client.domain.login

internal sealed interface LoginNews

internal data class OpenMainScreen(val userName: String) : LoginNews

internal data class ShowPortInvalidToast(val port: String) : LoginNews

internal data class ShowConnectionFailedToast(val message: String) : LoginNews
