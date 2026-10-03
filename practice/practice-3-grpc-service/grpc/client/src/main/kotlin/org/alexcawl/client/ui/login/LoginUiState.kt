package org.alexcawl.client.ui.login

internal sealed interface LoginUiState {

    data object Loading : LoginUiState

    data class Content(
        val host: String,
        val port: String,
        val userName: String,
    ) : LoginUiState
}
