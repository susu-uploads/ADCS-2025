package org.alexcawl.serializations.codec

import kotlinx.serialization.json.Json
import org.alexcawl.serializations.model.SampleModel

internal data object JsonSerializationCodec : SerializationCodec {
    private val json: Json = Json { encodeDefaults = true }

    override val id: String = "json-kotlinx"

    override fun serialize(payload: SampleModel): ByteArray {
        val encoded: String = json.encodeToString(SampleModel.serializer(), payload)
        return encoded.toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        val encoded: String = bytes.toString(Charsets.UTF_8)
        return json.decodeFromString(SampleModel.serializer(), encoded)
    }
}
