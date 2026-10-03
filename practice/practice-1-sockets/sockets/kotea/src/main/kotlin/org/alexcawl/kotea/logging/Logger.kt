package org.alexcawl.kotea.logging

import org.alexcawl.kotea.Next

public interface Logger<State : Any, Event : Any, Command : Any, News : Any> {

    public fun onInitialState(initialState: State)

    public fun onInitialCommands(initialCommands: List<Command>)

    public fun onEvent(event: Event)

    public fun onNext(next: Next<State, Command, News>)
}
