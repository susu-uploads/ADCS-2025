package org.alexcawl.sockets.client.domain.login

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.toPrettyString

internal class LoginLogger : Logger<LoginState, LoginEvent, LoginCommand, LoginNews> {

    override fun onInitialState(initialState: LoginState) {
        logger.info {
            buildString {
                appendLine(value = "[Login] Initial state:")
                append(initialState.toPrettyString())
            }
        }
    }

    override fun onInitialCommands(initialCommands: List<LoginCommand>) {
        logger.info {
            buildString {
                appendLine(value = "[Login] Initial commands:")
                append(initialCommands.toPrettyString())
            }
        }
    }

    override fun onEvent(event: LoginEvent) {
        logger.info {
            buildString {
                appendLine(value = "[Login] Event:")
                append(event.toPrettyString())
            }
        }
    }

    override fun onNext(next: Next<LoginState, LoginCommand, LoginNews>) {
        logger.info {
            buildString {
                appendLine(value = "[Login] Next:")
                append(next.toPrettyString())
            }
        }
    }
}
