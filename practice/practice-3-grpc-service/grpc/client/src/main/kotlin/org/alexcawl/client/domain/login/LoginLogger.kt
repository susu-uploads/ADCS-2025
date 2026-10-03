package org.alexcawl.client.domain.login

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger

internal class LoginLogger : Logger<LoginState, LoginEvent, LoginCommand, LoginNews> {

    override fun onInitialState(initialState: LoginState) {
        // No-op
    }

    override fun onInitialCommands(initialCommands: List<LoginCommand>) {
        // No-op
    }

    override fun onEvent(event: LoginEvent) {
        // No-op
    }

    override fun onNext(next: Next<LoginState, LoginCommand, LoginNews>) {
        // No-op
    }
}
