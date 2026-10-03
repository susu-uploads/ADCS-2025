package org.alexcawl.contract.users

public interface UsersServer {

    public suspend fun onAuthorizeUser(request: AuthorizeUserRequest): AuthorizeUserResponse
}
