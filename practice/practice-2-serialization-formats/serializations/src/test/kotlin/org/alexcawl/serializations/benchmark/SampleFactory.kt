package org.alexcawl.serializations.benchmark

import org.alexcawl.serializations.model.SampleItem
import org.alexcawl.serializations.model.SampleModel

internal object SampleFactory {
    internal fun create(itemCount: Int = 300): SampleModel {
        val items: List<SampleItem> = List(itemCount) { index: Int ->
            SampleItem(
                timestampEpochMs = 1_700_000_000_000L + index * 1_000L,
                host = "node-${index % 12}",
                load = ((index * 37) % 1000) / 10.0,
                errors = index % 5,
                tags = listOf("rack-${index % 4}", "zone-${index % 3}", "service-api")
            )
        }

        val labels: Map<String, String> = linkedMapOf(
            "environment" to "education",
            "project" to "arvs-serialization-benchmark",
            "group" to "СП-М-О-АРВС-2025"
        )

        return SampleModel(
            title = "Distributed systems serialization benchmark",
            version = 2,
            metrics = List(128) { index: Int -> ((index * 13) % 1000) / 10.0 },
            counts = List(128) { index: Int -> (index * 7) % 97 },
            labels = labels,
            items = items,
            notes = "Payload includes strings, arrays, maps, integers and doubles."
        )
    }
}
