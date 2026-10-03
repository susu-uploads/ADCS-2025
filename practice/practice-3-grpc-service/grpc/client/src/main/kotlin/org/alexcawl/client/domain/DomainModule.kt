package org.alexcawl.client.domain

import org.alexcawl.client.domain.login.LoginCommand
import org.alexcawl.client.domain.login.LoginEvent
import org.alexcawl.client.domain.login.LoginLogger
import org.alexcawl.client.domain.login.LoginNews
import org.alexcawl.client.domain.login.LoginState
import org.alexcawl.client.domain.login.LoginStore
import org.alexcawl.client.domain.login.LoginUpdate
import org.alexcawl.client.domain.main.AuthorizeUserCommandsFlowHandler
import org.alexcawl.client.domain.main.CommentPostCommandsFlowHandler
import org.alexcawl.client.domain.main.CreatePostCommandsFlowHandler
import org.alexcawl.client.domain.main.DislikePostCommandsFlowHandler
import org.alexcawl.client.domain.main.LikePostCommandsFlowHandler
import org.alexcawl.client.domain.main.LoadFeedCommandsFlowHandler
import org.alexcawl.client.domain.main.MainCommand
import org.alexcawl.client.domain.main.MainLogger
import org.alexcawl.client.domain.main.MainState
import org.alexcawl.client.domain.main.MainStore
import org.alexcawl.client.domain.main.MainUpdate
import org.alexcawl.client.domain.main.AuthorizeUserCommand
import org.alexcawl.client.domain.main.MainEvent
import org.alexcawl.client.domain.main.MainNews
import org.alexcawl.client.network.NetworkModule
import org.alexcawl.common.Container
import org.alexcawl.common.Provider
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.kotea.logging.Logger
import org.alexcawl.kotea.logging.NoOpLogger

internal class DomainModule(
    private val isVerbose: Boolean,
    private val defaultHost: String,
    private val defaultPort: Int,
) : Container {

    fun loginStore(): LoginStore {
        return LoginStore(
            initialState = loginState(),
            initialCommands = emptyList(),
            commandsFlowHandlers = emptyList(),
            update = LoginUpdate(),
            logger = loginLogger(),
        )
    }

    private val loginState: Provider<LoginState> by factory {
        LoginState(
            host = defaultHost,
            port = defaultPort.toString(),
            userName = "",
        )
    }

    private val loginLogger: Provider<Logger<LoginState, LoginEvent, LoginCommand, LoginNews>> by factory {
        if (isVerbose) LoginLogger() else NoOpLogger()
    }

    fun mainStore(userName: String, networkModule: NetworkModule): MainStore {
        val commandHandlers: List<CommandsFlowHandler<MainCommand, MainEvent>> = listOf(
            AuthorizeUserCommandsFlowHandler(usersClient = networkModule.usersClient),
            LoadFeedCommandsFlowHandler(postsClient = networkModule.postsClient),
            CreatePostCommandsFlowHandler(postsClient = networkModule.postsClient),
            LikePostCommandsFlowHandler(interactionsClient = networkModule.interactionsClient),
            DislikePostCommandsFlowHandler(interactionsClient = networkModule.interactionsClient),
            CommentPostCommandsFlowHandler(interactionsClient = networkModule.interactionsClient),
        )
        return MainStore(
            initialState = mainState(userName = userName),
            initialCommands = listOf(AuthorizeUserCommand(userName = userName)),
            commandsFlowHandlers = commandHandlers,
            update = MainUpdate(),
            logger = mainLogger(),
        )
    }

    private fun mainState(userName: String): MainState {
        return MainState(
            userId = null,
            userName = userName,
            posts = emptyList(),
            newPostText = "",
            commentDrafts = emptyMap(),
            isLoading = true,
        )
    }

    private val mainLogger: Provider<Logger<MainState, MainEvent, MainCommand, MainNews>> by factory {
        if (isVerbose) MainLogger() else NoOpLogger()
    }
}
