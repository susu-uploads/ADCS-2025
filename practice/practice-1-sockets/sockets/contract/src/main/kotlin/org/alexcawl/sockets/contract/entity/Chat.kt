package org.alexcawl.sockets.contract.entity

import kotlinx.serialization.Serializable

@Serializable
public data class Chat(
    val id: Int,
    val name: String,
    val type: ChatType,
)
