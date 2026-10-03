package org.alexcawl.client.domain.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.alexcawl.contract.users.AuthorizeUserRequest
import org.alexcawl.contract.users.AuthorizeUserResponse
import org.alexcawl.contract.users.UsersClient
import org.alexcawl.kotea.CommandsFlowHandler

internal class AuthorizeUserCommandsFlowHandler(
    private val usersClient: UsersClient,
) : CommandsFlowHandler<MainCommand, MainEvent> {

    override fun handle(commands: Flow<MainCommand>): Flow<MainEvent> {
        return commands
            .filterIsInstance<AuthorizeUserCommand>()
            .toEvents()
            .flowOn(Dispatchers.IO)
    }

    private fun Flow<AuthorizeUserCommand>.toEvents(): Flow<MainEvent> = flow {
        collect { command ->
            when (
                val response = usersClient.authorizeUser(
                    request = AuthorizeUserRequest(name = command.userName),
                )
            ) {
                is AuthorizeUserResponse.Success -> emit(
                    OnAuthorizedUserEvent(
                        userId = response.user.id,
                        userName = response.user.name,
                    ),
                )

                is AuthorizeUserResponse.ValidationFailure -> {
                    emit(OnAuthorizationFailedEvent(message = response.message))
                }
            }
        }
    }
}
