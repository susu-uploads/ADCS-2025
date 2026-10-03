package org.alexcawl.serializations.codec

import nl.adaptivity.xmlutil.serialization.XML
import org.alexcawl.serializations.model.SampleModel

internal data object XmlSerializationCodec : SerializationCodec {
    private val xml: XML = XML {}

    override val id: String = "xml-xmlutil"

    override fun serialize(payload: SampleModel): ByteArray {
        val encoded: String = xml.encodeToString(SampleModel.serializer(), payload)
        return encoded.toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        val encoded: String = bytes.toString(Charsets.UTF_8)
        return xml.decodeFromString(SampleModel.serializer(), encoded)
    }
}
