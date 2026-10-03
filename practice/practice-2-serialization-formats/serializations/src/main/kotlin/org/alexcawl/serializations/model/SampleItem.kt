package org.alexcawl.serializations.model

import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
internal data class SampleItem(
    val timestampEpochMs: Long,
    val host: String,
    val load: Double,
    val errors: Int,
    val tags: List<String>
) : JavaSerializable
