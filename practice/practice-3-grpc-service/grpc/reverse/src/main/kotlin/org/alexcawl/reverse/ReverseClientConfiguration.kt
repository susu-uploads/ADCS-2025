package org.alexcawl.reverse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReverseClientConfiguration(
    @SerialName(value = "reverse.host")
    val host: String = "127.0.0.1",
    @SerialName(value = "reverse.port")
    val port: Int = 50051,
)
