package org.alexcawl.server.controller

import org.alexcawl.contract.users.AuthorizeUserRequest
import org.alexcawl.contract.users.AuthorizeUserResponse
import org.alexcawl.contract.users.UsersServer
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase.Result

internal class UsersServerImpl(
    private val authorizeUserUseCase: AuthorizeUserUseCase,
) : UsersServer {

    override suspend fun onAuthorizeUser(request: AuthorizeUserRequest): AuthorizeUserResponse {
        return when (val result = authorizeUserUseCase(name = request.name)) {
            is Result.Success -> AuthorizeUserResponse.Success(user = result.user)
            is Result.ValidationFailure -> AuthorizeUserResponse.ValidationFailure(
                message = "User name must not be blank.",
            )
        }
    }
}
