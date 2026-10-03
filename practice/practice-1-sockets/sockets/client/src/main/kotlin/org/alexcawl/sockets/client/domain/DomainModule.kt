package org.alexcawl.sockets.client.domain

import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.kotea.logging.NoOpLogger
import org.alexcawl.sockets.client.data.UserIdGenerator
import org.alexcawl.sockets.client.domain.login.*
import org.alexcawl.sockets.client.domain.login.commandsFlowHandlers.EstablishConnectionCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.*
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.DecodeServerResponseCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.DisconnectNetworkCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.EncodeClientRequestCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.SendNetworkMessageCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.SubscribeOnNetworkMessagesCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.ChatCreateMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.ChatJoinMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.ChatLeaveMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.MessageSendMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.UserAuthorizeMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.UserChangeNameMainCommandsFlowHandler
import org.alexcawl.sockets.client.domain.main.commandsFlowHandlers.contract.UserDeauthorizeMainCommandsFlowHandler
import org.alexcawl.sockets.client.network.TcpNetwork
import org.alexcawl.sockets.common.Container
import org.alexcawl.sockets.common.Provider
import org.alexcawl.sockets.contract.Contract
import java.util.UUID

internal class DomainModule(
    private val isVerbose: Boolean,
    private val defaultHost: String,
    private val defaultPort: Int,
    private val clientFormat: Contract.ClientFormat,
    private val network: TcpNetwork,
    private val userIdGenerator: UserIdGenerator,
) : Container {

    fun loginStore(): LoginStore {
        val initialState: LoginState = initialLoginState()
        val commandsFlowHandlers: List<CommandsFlowHandler<LoginCommand, LoginEvent>> = loginStoreCommandsFlowHandlers()
        val logger: Logger<LoginState, LoginEvent, LoginCommand, LoginNews> = if (isVerbose) LoginLogger() else NoOpLogger()
        return LoginStore(
            initialState = initialState,
            initialCommands = emptyList(),
            commandsFlowHandlers = commandsFlowHandlers,
            update = LoginUpdate(),
            logger = logger,
        )
    }

    private val initialLoginState: Provider<LoginState> by factory {
        LoginState(
            host = defaultHost,
            port = defaultPort,
            userName = "",
        )
    }

    private val loginStoreCommandsFlowHandlers: Provider<List<EstablishConnectionCommandsFlowHandler>> by factory {
        listOf(
            EstablishConnectionCommandsFlowHandler(network = network),
        )
    }

    fun mainStore(userId: UUID?, userName: String?): MainStore {
        val initialState: MainState = defaultMainState()
        val initialCommands: List<MainCommand> = listOf(
            SubscribeOnNetworkMessagesCommand,
            UserAuthorizeCommand(userId = userId, userName = userName),
        )
        val commandsFlowHandlers: List<CommandsFlowHandler<MainCommand, MainEvent>> = mainStoreCommandsFlowHandlers()
        val logger: Logger<MainState, MainEvent, MainCommand, MainNews> = if (isVerbose) MainLogger() else NoOpLogger()
        return MainStore(
            initialState = initialState,
            initialCommands = initialCommands,
            commandsFlowHandlers = commandsFlowHandlers,
            update = MainUpdate(),
            logger = logger,
        )
    }

    private val defaultMainState: Provider<MainState> by factory {
        MainState.Loading
    }

    private val mainStoreCommandsFlowHandlers: Provider<List<CommandsFlowHandler<MainCommand, MainEvent>>> by factory {
        listOf(
            DecodeServerResponseCommandsFlowHandler(clientFormat = clientFormat),
            SubscribeOnNetworkMessagesCommandsFlowHandler(network = network),
            ChatCreateMainCommandsFlowHandler(),
            ChatJoinMainCommandsFlowHandler(),
            ChatLeaveMainCommandsFlowHandler(),
            MessageSendMainCommandsFlowHandler(),
            UserAuthorizeMainCommandsFlowHandler(userIdGenerator = userIdGenerator),
            UserChangeNameMainCommandsFlowHandler(),
            UserDeauthorizeMainCommandsFlowHandler(),
            EncodeClientRequestCommandsFlowHandler(clientFormat = clientFormat),
            SendNetworkMessageCommandsFlowHandler(network = network),
            DisconnectNetworkCommandsFlowHandler(network = network),
        )
    }
}
