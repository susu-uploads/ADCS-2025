package org.alexcawl.serializations.codec

import org.alexcawl.serializations.model.SampleModel

internal interface SerializationCodec {
    val id: String

    fun serialize(payload: SampleModel): ByteArray

    fun deserialize(bytes: ByteArray): SampleModel
}
