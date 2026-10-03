package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal interface TimestampFormatter {
    fun format(timestamp: Long): String
}

internal class TimestampFormatterImpl : TimestampFormatter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    override fun format(timestamp: Long): String {
        return Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(formatter)
    }
}
