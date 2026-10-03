package org.alexcawl.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationConfiguration(
    @SerialName("client.defaultHost")
    val defaultHost: String = "127.0.0.1",
    @SerialName("client.defaultPort")
    val defaultPort: Int = 50051,
    @SerialName("client.isVerbose")
    val isVerbose: Boolean = false,
)
