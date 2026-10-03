package org.alexcawl.serializations.benchmark

internal interface BenchmarkResultWriter {
    fun writeBenchmarkResult(result: BenchmarkResult)
}
