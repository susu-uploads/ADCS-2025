package org.alexcawl.serializations

import org.alexcawl.serializations.benchmark.SampleFactory
import org.alexcawl.serializations.codec.ProtobufSerializationCodec
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class ProtobufSerializationCodecTest {
    private val codec = ProtobufSerializationCodec
    private val sampleObject = SampleFactory.create()
    private val sampleByteArray: ByteArray = codec.serialize(sampleObject)

    @Test
    internal fun serialize(): Unit {
        val bytes: ByteArray = codec.serialize(sampleObject)

        assertContentEquals(sampleByteArray, bytes)
    }

    @Test
    internal fun deserialize(): Unit {
        val obj = codec.deserialize(sampleByteArray)

        assertEquals(sampleObject, obj)
    }

    @Test
    internal fun roundTrip(): Unit {
        val bytes: ByteArray = codec.serialize(sampleObject)
        val restoredObj = codec.deserialize(bytes)

        assertEquals(sampleObject, restoredObj)
    }
}
