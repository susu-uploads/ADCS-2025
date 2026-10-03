package org.alexcawl.serializations.benchmark

import java.util.*

internal object ConsoleBenchmarkWriter : BenchmarkResultWriter {
    private var headerPrinted: Boolean = false

    override fun writeBenchmarkResult(result: BenchmarkResult) {
        synchronized(this) {
            if (!headerPrinted) {
                println(
                    formatTableRow(
                        group = "group",
                        name = "name",
                        dataSize = "data_size",
                        samples = "samples",
                        avgNs = "avg_ns",
                        medianNs = "median_ns",
                        p95Ns = "p95_ns",
                        minNs = "min_ns",
                        maxNs = "max_ns",
                    )
                )
                headerPrinted = true
            }
            println(
                formatTableRow(
                    group = result.group,
                    name = result.name,
                    dataSize = result.dataSize.toString(),
                    samples = result.samples.toString(),
                    avgNs = formatDecimal(result.avgNs),
                    medianNs = result.medianNs.toString(),
                    p95Ns = result.p95Ns.toString(),
                    minNs = result.minNs.toString(),
                    maxNs = result.maxNs.toString(),
                )
            )
        }
    }

    private fun formatTableRow(
        group: String,
        name: String,
        dataSize: String,
        samples: String,
        avgNs: String,
        medianNs: String,
        p95Ns: String,
        minNs: String,
        maxNs: String,
    ): String {
        return String.format(
            locale = Locale.US,
            format = "%-20s %-16s %-12s %8s %14s %12s %12s %12s %12s",
            group,
            name,
            dataSize,
            samples,
            avgNs,
            medianNs,
            p95Ns,
            minNs,
            maxNs,
        )
    }

    private fun formatDecimal(value: Double): String = String.format(Locale.US, "%.3f", value)
}