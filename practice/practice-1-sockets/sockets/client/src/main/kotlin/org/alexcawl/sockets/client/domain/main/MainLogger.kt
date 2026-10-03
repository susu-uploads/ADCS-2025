package org.alexcawl.sockets.client.domain.main

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.sockets.common.logger
import org.alexcawl.sockets.common.toPrettyString

internal class MainLogger : Logger<MainState, MainEvent, MainCommand, MainNews> {

    override fun onInitialState(initialState: MainState) {
        logger.info {
            buildString {
                appendLine(value = "[Main] Initial state:")
                append(initialState.toPrettyString())
            }
        }
    }

    override fun onInitialCommands(initialCommands: List<MainCommand>) {
        logger.info {
            buildString {
                appendLine(value = "[Main] Initial commands:")
                append(initialCommands.toPrettyString())
            }
        }
    }

    override fun onEvent(event: MainEvent) {
        logger.info {
            buildString {
                appendLine(value = "[Main] Event:")
                append(event.toPrettyString())
            }
        }
    }

    override fun onNext(next: Next<MainState, MainCommand, MainNews>) {
        logger.info {
            buildString {
                appendLine(value = "[Main] Next:")
                append(next.toPrettyString())
            }
        }
    }
}
