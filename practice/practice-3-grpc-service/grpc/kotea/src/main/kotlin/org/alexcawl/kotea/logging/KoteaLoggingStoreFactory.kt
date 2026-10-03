package org.alexcawl.kotea.logging

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.KoteaStore
import org.alexcawl.kotea.Next
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.Update

/**
 * Factory for logging store, which prints updates for states, commands, events and news via logger.
 */
public fun <State : Any, Event : Any, UiEvent : Event, Command : Any, News : Any> KoteaLoggingStore(
    initialState: State,
    initialCommands: List<Command> = emptyList(),
    commandsFlowHandlers: List<CommandsFlowHandler<Command, Event>> = emptyList(),
    update: Update<State, Event, Command, News> = Update { _, _ -> Next() },
    logger: Logger<State, Event, Command, News> = NoOpLogger(),
): Store<State, UiEvent, News> {
    logger.onInitialState(initialState = initialState)
    if (initialCommands.isNotEmpty()) {
        logger.onInitialCommands(initialCommands = initialCommands)
    }
    return KoteaStore(
        initialState = initialState,
        initialCommands = initialCommands,
        commandsFlowHandlers = commandsFlowHandlers,
        update = LoggingUpdate(
            delegate = update,
            logger = logger,
        ),
    )
}
