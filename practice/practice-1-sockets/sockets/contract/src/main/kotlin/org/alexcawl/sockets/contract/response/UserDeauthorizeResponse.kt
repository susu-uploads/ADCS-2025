package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.User

@Serializable
public sealed interface UserDeauthorizeResponse : ServerResponse {

    @Serializable
    public data class Success(val user: User) : UserDeauthorizeResponse
}
