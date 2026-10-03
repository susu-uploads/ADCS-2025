package org.alexcawl.sockets.client.domain.main

internal sealed interface MainNews

internal data class ShowMainToast(val message: String) : MainNews

internal data object OpenLoginScreen : MainNews
