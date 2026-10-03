package org.alexcawl.common

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class LoggerDelegate<T : Any> : ReadOnlyProperty<T, Logger> {
    override fun getValue(thisRef: T, property: KProperty<*>): Logger {
        return Logger.getLogger(thisRef::class.simpleName).apply {
            useParentHandlers = false
            if (handlers.none { it is LoggerHandler }) {
                addHandler(LoggerHandler)
            }
        }
    }
}

public fun <T : Any> logger(): ReadOnlyProperty<T, Logger> {
    return LoggerDelegate()
}

public val <T : Any> T.logger: Logger by LoggerDelegate()

public fun Logger.warning(message: String, exception: Throwable) {
    log(Level.WARNING, message, exception)
}

public fun Logger.severe(message: String, exception: Throwable) {
    log(Level.SEVERE, message, exception)
}

private object LoggerHandler : ConsoleHandler() {
    init {
        formatter = LoggerFormatter
    }
}

private object LoggerFormatter : Formatter() {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    override fun format(record: LogRecord): String {
        val time: String? = dateTimeFormatter.format(record.instant)
        val message: String? = formatMessage(record)
        return "[$time] [${record.level.name}] $message\n\n"
    }
}
