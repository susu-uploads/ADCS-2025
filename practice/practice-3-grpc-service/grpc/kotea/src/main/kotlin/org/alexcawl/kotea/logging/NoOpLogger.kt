package org.alexcawl.kotea.logging

import org.alexcawl.kotea.Next

public class NoOpLogger<State : Any, Event : Any, Command : Any, News : Any> : Logger<State, Event, Command, News> {

    override fun onInitialState(initialState: State) {
        // No-op
    }

    override fun onInitialCommands(initialCommands: List<Command>) {
        // No-op
    }

    override fun onEvent(event: Event) {
        // No-op
    }

    override fun onNext(next: Next<State, Command, News>) {
        // No-op
    }
}
