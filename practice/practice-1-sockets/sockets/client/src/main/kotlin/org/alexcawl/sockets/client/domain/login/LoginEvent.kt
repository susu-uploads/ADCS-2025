package org.alexcawl.sockets.client.domain.login

internal sealed interface LoginEvent

internal data object OnConnectedEvent : LoginEvent

internal data class OnConnectFailedEvent(val cause: Throwable) : LoginEvent


internal sealed interface LoginUiEvent : LoginEvent

internal data class OnHostChangedUiEvent(val host: String) : LoginUiEvent

internal data class OnPortChangedUiEvent(val port: String) : LoginUiEvent

internal data class OnUserNameChangedUiEvent(val userName: String) : LoginUiEvent

internal data object OnConnectClickedUiEvent : LoginUiEvent
