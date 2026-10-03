package org.alexcawl.sockets.contract.entity

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.UUIDSerializer
import java.util.UUID

@Serializable
public data class User(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val type: UserType,
    val connectionType: UserConnectionType,
)
