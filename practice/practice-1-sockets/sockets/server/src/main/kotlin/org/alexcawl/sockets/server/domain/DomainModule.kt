package org.alexcawl.sockets.server.domain

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.Update
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.kotea.logging.NoOpLogger
import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.common.Provider
import org.alexcawl.sockets.contract.Contract
import org.alexcawl.sockets.server.domain.global.CreateSystemUserCommand
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.GlobalIntent
import org.alexcawl.sockets.server.domain.global.GlobalLogger
import org.alexcawl.sockets.server.domain.global.GlobalState
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.GlobalUpdate
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.CreateSystemChatGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.CreateSystemUserGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.ChatCreateGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.ChatJoinGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.ChatLeaveGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.MessageSendGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.TimestampFormatter
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.TimestampFormatterImpl
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.UserAuthorizeGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.UserChangeNameGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract.UserDeauthorizeGlobalCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.SessionNews
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.SessionLogger
import org.alexcawl.sockets.server.domain.session.SessionState
import org.alexcawl.sockets.server.domain.session.SessionStore
import org.alexcawl.sockets.server.domain.session.SessionUpdate
import org.alexcawl.sockets.server.domain.session.SubscribeOnGlobalUpdatesCommand
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.DecodeClientRequestCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.EncodeServerResponseCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.SubscribeOnGlobalUpdatesCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.ChatCreateSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.ChatJoinSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.ChatLeaveSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.MessageSendSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UpdateChatMembersSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UpdateChatSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UpdateChatsSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UpdateUsersSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UserAuthorizeSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UserChangeNameSessionCommandsFlowHandler
import org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract.UserDeauthorizeSessionCommandsFlowHandler
import org.jetbrains.exposed.sql.Database

internal class DomainModule(
    private val isEcho: Boolean,
    private val isVerbose: Boolean,
    private val systemUserName: String,
    private val systemChatName: String,
    private val serverFormat: Contract.ServerFormat,
    private val database: Database,
) : Container {

    val sessionStore: Provider<SessionStore> by factory {
        val initialState: SessionState = initialSessionState()
        val commandsFlowHandlers: List<CommandsFlowHandler<SessionCommand, SessionEvent>> = sessionCommandsFlowHandlers()
        val update: Update<SessionState, SessionEvent, SessionCommand, SessionNews> = SessionUpdate(serverIsEcho = isEcho)
        val logger: Logger<SessionState, SessionEvent, SessionCommand, SessionNews> = if (isVerbose) SessionLogger() else NoOpLogger()
        SessionStore(
            initialState = initialState,
            initialCommands = listOf(SubscribeOnGlobalUpdatesCommand),
            commandsFlowHandlers = commandsFlowHandlers,
            update = update,
            logger = logger,
        )
    }

    private val initialSessionState: Provider<SessionState> by factory {
        SessionState.NotAuthorized
    }

    private val sessionCommandsFlowHandlers: Provider<List<CommandsFlowHandler<SessionCommand, SessionEvent>>> by factory {
        listOf(
            DecodeClientRequestCommandsFlowHandler(serverFormat = serverFormat),
            UserAuthorizeSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            UserDeauthorizeSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            UserChangeNameSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            ChatCreateSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            ChatJoinSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            ChatLeaveSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            MessageSendSessionCommandsFlowHandler(globalStore = globalStore, database = database),
            EncodeServerResponseCommandsFlowHandler(serverFormat = serverFormat),
            SubscribeOnGlobalUpdatesCommandsFlowHandler(globalStore = globalStore),
            UpdateChatSessionCommandsFlowHandler(database = database),
            UpdateChatMembersSessionCommandsFlowHandler(database = database),
            UpdateChatsSessionCommandsFlowHandler(database = database),
            UpdateUsersSessionCommandsFlowHandler(database = database),
        )
    }

    private val timestampFormatter: Provider<TimestampFormatter> by factory {
        TimestampFormatterImpl()
    }

    val globalStore: GlobalStore by single {
        val initialState: GlobalState = initialGlobalState()
        val commandsFlowHandlers: List<CommandsFlowHandler<GlobalCommand, GlobalEvent>> = globalCommandsFlowHandlers()
        val logger: Logger<GlobalState, GlobalEvent, GlobalCommand, GlobalIntent> = if (isVerbose) GlobalLogger() else NoOpLogger()
        GlobalStore(
            initialState = initialState,
            initialCommands = listOf(CreateSystemUserCommand),
            commandsFlowHandlers = commandsFlowHandlers,
            update = GlobalUpdate(),
            logger = logger,
        )
    }

    private val initialGlobalState: Provider<GlobalState> by factory {
        GlobalState()
    }

    private val globalCommandsFlowHandlers: Provider<List<CommandsFlowHandler<GlobalCommand, GlobalEvent>>> by factory {
        val timestampFormatter: TimestampFormatter = timestampFormatter()
        listOf(
            CreateSystemUserGlobalCommandsFlowHandler(systemUserName = systemUserName, database = database),
            CreateSystemChatGlobalCommandsFlowHandler(systemChatName = systemChatName, database = database),
            ChatCreateGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
            ChatJoinGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
            ChatLeaveGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
            MessageSendGlobalCommandsFlowHandler(),
            UserAuthorizeGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
            UserChangeNameGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
            UserDeauthorizeGlobalCommandsFlowHandler(timestampFormatter = timestampFormatter, database = database),
        )
    }
}
