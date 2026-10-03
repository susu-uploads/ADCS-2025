package org.alexcawl.serializations.codec

import org.alexcawl.serializations.model.SampleModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

internal data object NativeSerializationCodec : SerializationCodec {
    override val id: String = "native-java"

    override fun serialize(payload: SampleModel): ByteArray {
        return ByteArrayOutputStream().use { outputBuffer: ByteArrayOutputStream ->
            ObjectOutputStream(outputBuffer).use { stream: ObjectOutputStream ->
                stream.writeObject(payload)
            }
            outputBuffer.toByteArray()
        }
    }

    override fun deserialize(bytes: ByteArray): SampleModel {
        return ByteArrayInputStream(bytes).use { inputBuffer: ByteArrayInputStream ->
            ObjectInputStream(inputBuffer).use { stream: ObjectInputStream ->
                stream.readObject() as SampleModel
            }
        }
    }
}
