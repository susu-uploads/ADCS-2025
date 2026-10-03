package org.alexcawl.serializations.benchmark

import org.alexcawl.serializations.codec.SerializationCodec
import kotlin.collections.plusAssign
import kotlin.system.measureNanoTime

internal abstract class SerializationCodecBenchmarkTest {

    protected abstract val codec: SerializationCodec

    protected abstract val sampleObject: Any

    protected abstract val sampleByteArray: ByteArray

    private val benchmarkResultWriters: List<BenchmarkResultWriter> = listOf(
        ConsoleBenchmarkWriter,
        AggregatedCsvBenchmarkWriter,
    )

    protected fun runBenchmark(name: String, iterations: Int = 100, block: () -> Unit) {
        require(iterations >= 1) { "iterations must be >= 1." }

        val measuredNanos: MutableList<Long> = ArrayList(iterations)
        repeat(times = iterations) {
            measuredNanos += measureNanoTime(block)
        }

        val result: BenchmarkResult = BenchmarkResult.create(
            group = codec.id,
            name = name,
            dataSize = sampleByteArray.size,
            measuredNanos = measuredNanos,
        )
        benchmarkResultWriters.forEach { writer: BenchmarkResultWriter ->
            writer.writeBenchmarkResult(result = result)
        }
    }
}
