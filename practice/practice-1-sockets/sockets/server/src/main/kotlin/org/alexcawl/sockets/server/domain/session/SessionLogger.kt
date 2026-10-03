package org.alexcawl.sockets.server.domain.session

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.toPrettyString

internal class SessionLogger : Logger<SessionState, SessionEvent, SessionCommand, SessionNews> {

    private val id: String = hashCode().toString()

    override fun onInitialState(initialState: SessionState) {
        logger.info {
            buildString {
                appendLine(value = "[Session-$id] Initial state:")
                append(initialState.toPrettyString())
            }
        }
    }

    override fun onInitialCommands(initialCommands: List<SessionCommand>) {
        logger.info {
            buildString {
                appendLine(value = "[Session-$id] Initial commands:")
                append(initialCommands.toPrettyString())
            }
        }
    }

    override fun onEvent(event: SessionEvent) {
        logger.info {
            buildString {
                appendLine(value = "[Session-$id] Event:")
                append(event.toPrettyString())
            }
        }
    }

    override fun onNext(next: Next<SessionState, SessionCommand, SessionNews>) {
        logger.info {
            buildString {
                appendLine(value = "[Session-$id] Next:")
                append(next.toPrettyString())
            }
        }
    }
}
