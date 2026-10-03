package org.alexcawl.sockets.contract.entity

import kotlinx.serialization.Serializable
import org.alexcawl.sockets.contract.UUIDSerializer
import java.util.UUID

@Serializable
public data class Message(
    val id: Int,
    @Serializable(with = UUIDSerializer::class) val authorId: UUID,
    val chatId: Int,
    val text: String,
    val timestamp: Long,
    val type: MessageType,
)
