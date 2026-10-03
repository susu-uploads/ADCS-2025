package org.alexcawl.sockets.client.ui.login

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface LoginUiState {

    @Immutable
    data object Loading : LoginUiState

    @Immutable
    data class Content(
        val host: String,
        val port: String,
        val userName: String,
    ) : LoginUiState
}
