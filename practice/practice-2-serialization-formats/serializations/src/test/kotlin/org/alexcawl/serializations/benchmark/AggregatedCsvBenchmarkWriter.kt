package org.alexcawl.serializations.benchmark

import java.io.File
import java.util.Locale

internal object AggregatedCsvBenchmarkWriter : BenchmarkResultWriter {
    private val aggregateReportFile: File = File("build/reports/all-benchmarks.csv")

    override fun writeBenchmarkResult(result: BenchmarkResult) {
        synchronized(this) {
            aggregateReportFile.parentFile?.mkdirs()
            if (!aggregateReportFile.exists() || aggregateReportFile.length() == 0L) {
                aggregateReportFile.appendText("group;name;data_size;samples;avg_ns;median_ns;p95_ns;min_ns;max_ns${System.lineSeparator()}")
            }
            aggregateReportFile.appendText(
                "${result.group};" +
                "${result.name};" +
                "${result.dataSize};" +
                "${result.samples};" +
                "${formatDecimal(result.avgNs)};" +
                "${result.medianNs};" +
                "${result.p95Ns};" +
                "${result.minNs};" +
                "${result.maxNs};" +
                System.lineSeparator()
            )
        }
    }

    private fun formatDecimal(value: Double): String = String.format(Locale.US, "%.3f", value)
}