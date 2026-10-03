package org.alexcawl.client.domain.login

internal sealed interface LoginEvent

internal sealed interface LoginUiEvent : LoginEvent

internal data class OnHostChangedUiEvent(val host: String) : LoginUiEvent

internal data class OnPortChangedUiEvent(val port: String) : LoginUiEvent

internal data class OnUserNameChangedUiEvent(val userName: String) : LoginUiEvent

internal data object OnAuthorizeClickedUiEvent : LoginUiEvent
