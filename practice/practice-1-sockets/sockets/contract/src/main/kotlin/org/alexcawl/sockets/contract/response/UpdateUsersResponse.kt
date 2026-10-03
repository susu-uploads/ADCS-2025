package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.entity.User

@Serializable
public data class UpdateUsersResponse(val users: List<User>) : UpdateResponse
