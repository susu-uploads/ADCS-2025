package org.alexcawl.serializations.codec

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.alexcawl.serializations.model.SampleModel

@OptIn(ExperimentalSerializationApi::class)
internal data object ProtobufSerializationCodec : SerializationCodec {
    private val protoBuf: ProtoBuf = ProtoBuf { encodeDefaults = true }

    override val id: String = "protobuf-kotlinx"

    override fun serialize(payload: SampleModel): ByteArray {
        return protoBuf.encodeToByteArray(SampleModel.serializer(), payload)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        return protoBuf.decodeFromByteArray(SampleModel.serializer(), bytes)
    }
}
