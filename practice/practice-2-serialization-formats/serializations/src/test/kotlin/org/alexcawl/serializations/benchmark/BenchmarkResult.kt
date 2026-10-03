package org.alexcawl.serializations.benchmark

import kotlin.math.ceil

internal data class BenchmarkResult(
    val group: String,
    val name: String,
    val dataSize: Int,
    val samples: Int,
    val avgNs: Double,
    val medianNs: Long,
    val p95Ns: Long,
    val minNs: Long,
    val maxNs: Long,
) {
    companion object {
        fun create(group: String, name: String, dataSize: Int, measuredNanos: List<Long>): BenchmarkResult {
            return BenchmarkResult(
                group = group,
                name = name,
                dataSize = dataSize,
                samples = measuredNanos.size,
                avgNs = measuredNanos.average(),
                medianNs = percentile(measuredNanos, 0.5),
                p95Ns = percentile(measuredNanos, 0.95),
                minNs = measuredNanos.minOrNull() ?: 0L,
                maxNs = measuredNanos.maxOrNull() ?: 0L,
            )
        }
    }
}

private fun percentile(values: List<Long>, p: Double): Long {
    if (values.isEmpty()) {
        return 0L
    }
    val sorted: List<Long> = values.sorted()
    val index: Int = ceil(p * sorted.size).toInt().coerceIn(1, sorted.size) - 1
    return sorted[index]
}
