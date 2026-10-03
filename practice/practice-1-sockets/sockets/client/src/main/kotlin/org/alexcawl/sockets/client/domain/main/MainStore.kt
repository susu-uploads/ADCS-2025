package org.alexcawl.sockets.client.domain.main

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.Update
import org.alexcawl.kotea.logging.KoteaLoggingStore
import org.alexcawl.kotea.logging.Logger

internal class MainStore(
    initialState: MainState,
    initialCommands: List<MainCommand>,
    commandsFlowHandlers: List<CommandsFlowHandler<MainCommand, MainEvent>>,
    update: Update<MainState, MainEvent, MainCommand, MainNews>,
    logger: Logger<MainState, MainEvent, MainCommand, MainNews>,
) : Store<MainState, MainUiEvent, MainNews> by KoteaLoggingStore(
    initialState = initialState,
    initialCommands = initialCommands,
    commandsFlowHandlers = commandsFlowHandlers,
    update = update,
    logger = logger,
)
