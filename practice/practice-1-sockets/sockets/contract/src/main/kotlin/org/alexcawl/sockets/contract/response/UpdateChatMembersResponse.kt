package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.UUIDSerializer
import java.util.UUID

@Serializable
public data class UpdateChatMembersResponse(
    val chatId: Int,
    val memberIds: List<@Serializable(with = UUIDSerializer::class) UUID>,
) : UpdateResponse
