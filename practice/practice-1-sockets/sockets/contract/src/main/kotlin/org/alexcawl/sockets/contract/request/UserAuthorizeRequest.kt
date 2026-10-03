package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.UUIDSerializer
import java.util.UUID

@Serializable
public data class UserAuthorizeRequest(
    @Serializable(with = UUIDSerializer::class) val userId: UUID,
    val userName: String? = null,
) : ClientRequest
