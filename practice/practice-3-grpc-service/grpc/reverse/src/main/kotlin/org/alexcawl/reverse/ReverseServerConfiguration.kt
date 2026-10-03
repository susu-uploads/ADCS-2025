package org.alexcawl.reverse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReverseServerConfiguration(
    @SerialName(value = "reverse.port")
    val port: Int = 50051,
)
