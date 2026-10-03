package org.alexcawl.serializations

import org.alexcawl.serializations.benchmark.SerializationCodecBenchmarkTest
import org.alexcawl.serializations.benchmark.SampleFactory
import org.alexcawl.serializations.codec.YamlSerializationCodec
import org.alexcawl.serializations.model.SampleModel
import org.junit.jupiter.api.Test

internal class YamlSerializationCodecBenchmarkTest : SerializationCodecBenchmarkTest() {
    override val codec: YamlSerializationCodec = YamlSerializationCodec
    override val sampleObject: SampleModel = SampleFactory.create()
    override val sampleByteArray: ByteArray = codec.serialize(sampleObject)

    @Test
    internal fun serialize() {
        runBenchmark(name = "serialize") {
            codec.serialize(sampleObject)
        }
    }

    @Test
    internal fun deserialize() {
        runBenchmark(name = "deserialize") {
            codec.deserialize(sampleByteArray)
        }
    }

    @Test
    internal fun roundTrip() {
        runBenchmark(name = "roundTrip") {
            codec.deserialize(codec.serialize(sampleObject))
        }
    }
}
