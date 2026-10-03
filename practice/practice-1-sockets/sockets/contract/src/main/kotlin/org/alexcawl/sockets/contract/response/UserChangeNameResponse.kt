package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.User

@Serializable
public sealed interface UserChangeNameResponse : ServerResponse {

    @Serializable
    public data class Success(val user: User) : UserChangeNameResponse

    @Serializable
    public data object UserNotFound : UserChangeNameResponse
}
