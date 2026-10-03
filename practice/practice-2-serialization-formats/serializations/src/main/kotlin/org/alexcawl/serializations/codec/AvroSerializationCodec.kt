package org.alexcawl.serializations.codec

import com.github.avrokotlin.avro4k.Avro
import org.alexcawl.serializations.model.SampleModel

internal data object AvroSerializationCodec : SerializationCodec {
    override val id: String = "avro-avro4k"

    override fun serialize(payload: SampleModel): ByteArray {
        return Avro.encodeToByteArray(SampleModel.serializer(), payload)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        return Avro.decodeFromByteArray(SampleModel.serializer(), bytes)
    }
}
