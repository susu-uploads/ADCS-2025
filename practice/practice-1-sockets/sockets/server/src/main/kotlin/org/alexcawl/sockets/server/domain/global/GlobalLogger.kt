package org.alexcawl.sockets.server.domain.global

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.toPrettyString

internal class GlobalLogger : Logger<GlobalState, GlobalEvent, GlobalCommand, GlobalIntent> {

    override fun onInitialState(initialState: GlobalState) {
        logger.info {
            buildString {
                appendLine(value = "[Global] Initial state:")
                append(initialState.toPrettyString())
            }
        }
    }

    override fun onInitialCommands(initialCommands: List<GlobalCommand>) {
        logger.info {
            buildString {
                appendLine(value = "[Global] Initial commands:")
                append(initialCommands.toPrettyString())
            }
        }
    }

    override fun onEvent(event: GlobalEvent) {
        logger.info {
            buildString {
                appendLine(value = "[Global] Event:")
                append(event.toPrettyString())
            }
        }
    }

    override fun onNext(next: Next<GlobalState, GlobalCommand, GlobalIntent>) {
        logger.info {
            buildString {
                appendLine(value = "[Global] Next:")
                append(next.toPrettyString())
            }
        }
    }
}
