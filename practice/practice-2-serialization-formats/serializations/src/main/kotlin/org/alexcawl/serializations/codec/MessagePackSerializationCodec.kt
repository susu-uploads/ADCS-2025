package org.alexcawl.serializations.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.alexcawl.serializations.model.SampleModel
import org.msgpack.jackson.dataformat.MessagePackFactory

internal data object MessagePackSerializationCodec : SerializationCodec {
    private val mapper: ObjectMapper = ObjectMapper(MessagePackFactory()).registerKotlinModule()

    override val id: String = "messagepack-jackson"

    override fun serialize(payload: SampleModel): ByteArray {
        return mapper.writeValueAsBytes(payload)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        return mapper.readValue(bytes, SampleModel::class.java)
    }
}
