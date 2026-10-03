package org.alexcawl.sockets.server.domain.session

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.Update
import org.alexcawl.kotea.logging.KoteaLoggingStore
import org.alexcawl.kotea.logging.Logger

internal class SessionStore(
    initialState: SessionState,
    initialCommands: List<SessionCommand>,
    commandsFlowHandlers: List<CommandsFlowHandler<SessionCommand, SessionEvent>>,
    update: Update<SessionState, SessionEvent, SessionCommand, SessionNews>,
    logger: Logger<SessionState, SessionEvent, SessionCommand, SessionNews>,
) : Store<SessionState, SocketEvent, SessionNews> by KoteaLoggingStore(
    initialState = initialState,
    initialCommands = initialCommands,
    commandsFlowHandlers = commandsFlowHandlers,
    update = update,
    logger = logger,
)
