package org.alexcawl.serializations.model

import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
internal data class SampleModel(
    val title: String,
    val version: Int,
    val metrics: List<Double>,
    val counts: List<Int>,
    val labels: Map<String, String>,
    val items: List<SampleItem>,
    val notes: String
) : JavaSerializable

