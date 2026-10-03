package org.alexcawl.sockets.server.domain.global

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.Update
import org.alexcawl.kotea.logging.KoteaLoggingStore
import org.alexcawl.kotea.logging.Logger

internal class GlobalStore(
    initialState: GlobalState,
    initialCommands: List<GlobalCommand>,
    commandsFlowHandlers: List<CommandsFlowHandler<GlobalCommand, GlobalEvent>>,
    update: Update<GlobalState, GlobalEvent, GlobalCommand, GlobalIntent>,
    logger: Logger<GlobalState, GlobalEvent, GlobalCommand, GlobalIntent>,
) : Store<GlobalState, ActionEvent, GlobalIntent> by KoteaLoggingStore(
    initialState = initialState,
    initialCommands = initialCommands,
    commandsFlowHandlers = commandsFlowHandlers,
    update = update,
    logger = logger,
)
