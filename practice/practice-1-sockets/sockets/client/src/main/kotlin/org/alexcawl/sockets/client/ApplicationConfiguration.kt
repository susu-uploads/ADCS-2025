package org.alexcawl.sockets.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationConfiguration(
    @SerialName(value = "client.defaultHost")
    val defaultHost: String = "127.0.0.1",
    @SerialName(value = "client.defaultPort")
    val defaultPort: Int = 8080,
    @SerialName(value = "client.isVerbose")
    val isVerbose: Boolean = false,
    @SerialName(value = "client.timeoutMs")
    val timeoutMs: Int = 0,
)
