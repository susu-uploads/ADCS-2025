package org.alexcawl.sockets.contract.entity

import kotlinx.serialization.Serializable

@Serializable
public enum class ChatType {
    READ_ONLY,
    DEFAULT,
}
