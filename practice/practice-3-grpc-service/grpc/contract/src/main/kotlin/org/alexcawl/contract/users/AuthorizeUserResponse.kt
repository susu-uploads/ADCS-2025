package org.alexcawl.contract.users

import org.alexcawl.contract.entity.User

public sealed interface AuthorizeUserResponse {

    public data class Success(
        public val user: User,
    ) : AuthorizeUserResponse

    public data class ValidationFailure(
        public val message: String,
    ) : AuthorizeUserResponse
}
