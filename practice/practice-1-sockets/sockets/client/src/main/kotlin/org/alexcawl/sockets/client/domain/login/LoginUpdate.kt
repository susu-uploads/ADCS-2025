package org.alexcawl.sockets.client.domain.login

import org.alexcawl.kotea.DslUpdate
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.warning

internal class LoginUpdate : DslUpdate<LoginState, LoginEvent, LoginCommand, LoginNews>() {

    override fun NextBuilder.update(event: LoginEvent) {
        when (event) {
            is OnConnectClickedUiEvent -> {
                commands(
                    EstablishConnection(
                        host = state.host,
                        port = state.port,
                    ),
                )
            }
            is OnHostChangedUiEvent -> {
                state {
                    copy(host = event.host)
                }
            }
            is OnPortChangedUiEvent -> {
                val port = event.port.toIntOrNull()
                if (port != null) {
                    state {
                        copy(port = port)
                    }
                } else {
                    news(
                        ShowPortInvalidToast(port = event.port)
                    )
                }
            }
            is OnUserNameChangedUiEvent -> {
                state {
                    copy(userName = event.userName)
                }
            }

            is OnConnectedEvent -> {
                news(
                    OpenMainScreen(userName = state.userName)
                )
            }

            is OnConnectFailedEvent -> {
                news(
                    ShowConnectionFailedToast(message = event.cause.message ?: "Unknown error")
                )
                logger.warning(message = "Connection failed", exception = event.cause)
            }
        }
    }
}
