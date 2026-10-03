package org.alexcawl.client.domain.login
import org.alexcawl.kotea.DslUpdate

internal class LoginUpdate : DslUpdate<LoginState, LoginEvent, LoginCommand, LoginNews>() {

    override fun NextBuilder.update(event: LoginEvent) {
        when (event) {
            is OnHostChangedUiEvent -> state { copy(host = event.host) }
            is OnPortChangedUiEvent -> state { copy(port = event.port) }
            is OnUserNameChangedUiEvent -> state { copy(userName = event.userName) }

            OnAuthorizeClickedUiEvent -> {
                val portValue: Int = state.port.toIntOrNull()
                    ?: run {
                        news(ShowInvalidPortToast)
                        return
                }
                val userName: String = state.userName.trim()
                if (state.host.isBlank() || userName.isBlank()) {
                    news(ShowBlankHostOrUserNameToast)
                    return
                }
                state { copy(userName = userName) }
                news(
                    OpenMainScreen(
                        host = state.host.trim(),
                        port = portValue,
                        userName = userName,
                    ),
                )
            }
        }
    }
}
