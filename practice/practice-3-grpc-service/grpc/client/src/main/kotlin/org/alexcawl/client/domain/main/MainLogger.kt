package org.alexcawl.client.domain.main

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.logging.Logger

internal class MainLogger : Logger<MainState, MainEvent, MainCommand, MainNews> {

    override fun onInitialState(initialState: MainState) {
        // No-op
    }

    override fun onInitialCommands(initialCommands: List<MainCommand>) {
        // No-op
    }

    override fun onEvent(event: MainEvent) {
        // No-op
    }

    override fun onNext(next: Next<MainState, MainCommand, MainNews>) {
        // No-op
    }
}
