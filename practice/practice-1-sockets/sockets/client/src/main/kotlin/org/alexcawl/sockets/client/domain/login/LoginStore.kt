package org.alexcawl.sockets.client.domain.login

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.Update
import org.alexcawl.kotea.logging.KoteaLoggingStore
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.kotea.logging.NoOpLogger

internal class LoginStore(
    initialState: LoginState,
    initialCommands: List<LoginCommand>,
    commandsFlowHandlers: List<CommandsFlowHandler<LoginCommand, LoginEvent>>,
    update: Update<LoginState, LoginEvent, LoginCommand, LoginNews>,
    logger: Logger<LoginState, LoginEvent, LoginCommand, LoginNews>,
) : Store<LoginState, LoginUiEvent, LoginNews> by KoteaLoggingStore(
    initialState = initialState,
    initialCommands = initialCommands,
    commandsFlowHandlers = commandsFlowHandlers,
    update = update,
    logger = logger,
)
