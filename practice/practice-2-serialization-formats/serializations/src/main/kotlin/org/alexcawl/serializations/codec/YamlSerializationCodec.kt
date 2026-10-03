package org.alexcawl.serializations.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import org.alexcawl.serializations.model.SampleModel

internal data object YamlSerializationCodec : SerializationCodec {
    private val mapper: ObjectMapper = ObjectMapper(
        YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    ).registerKotlinModule()

    override val id: String = "yaml-jackson"

    override fun serialize(payload: SampleModel): ByteArray {
        return mapper.writeValueAsBytes(payload)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        return mapper.readValue(bytes, SampleModel::class.java)
    }
}
