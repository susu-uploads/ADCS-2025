package org.alexcawl.sockets.common

import java.lang.reflect.Modifier
import java.util.IdentityHashMap

/**
 * Strategy interface for converting arbitrary objects into a structured,
 * human-readable multi-line string representation.
 */
private interface PrettyStringFormatter {

    /**
     * Formats the given value into a pretty-printed string.
     *
     * @param value root object to render
     * @param indent indentation unit used per nesting level
     * @param initialIndent prefix applied to every resulting line
     * @param maxDepth maximum recursion depth to prevent runaway traversal
     */
    fun format(
        value: Any?,
        indent: String,
        initialIndent: String,
        maxDepth: Int,
    ): String
}

/**
 * Default reflection-based implementation.
 *
 * Key properties:
 * - Uses IdentityHashMap to track object identity (not equals()).
 * - Prevents infinite recursion on cyclic graphs.
 * - Caches printer lambdas to allow forward references.
 * - Avoids reflective access into JDK / Kotlin core classes (JPMS-safe).
 */
private class DefaultPrettyStringFormatter : PrettyStringFormatter {

    /**
     * Represents lifecycle of a node during formatting.
     *
     * Building – currently traversing fields of this object.
     * Built    – fully rendered and cached.
     */
    private enum class NodeState {
        Building,
        Built,
    }

    override fun format(
        value: Any?,
        indent: String,
        initialIndent: String,
        maxDepth: Int,
    ): String {
        // Stores object -> printer lambda (forward reference support).
        val printers: IdentityHashMap<Any, () -> String> = IdentityHashMap()

        // Stores object -> current build state.
        val states: IdentityHashMap<Any, NodeState> = IdentityHashMap()

        val raw: String = formatValue(
            value = value,
            level = 0,
            indent = indent,
            printers = printers,
            states = states,
            maxDepth = maxDepth,
        )

        return applyInitialIndent(
            text = raw,
            initialIndent = initialIndent,
        )
    }

    /**
     * Core recursive dispatcher.
     *
     * Order of handling:
     * 1) null / depth guard
     * 2) simple primitives
     * 3) containers
     * 4) cached object / cycle detection
     * 5) reflective object formatting
     */
    private fun formatValue(
        value: Any?,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        //
        if (value == null) return "null"
        if (level > maxDepth) return "...(maxDepth reached)"

        // Primitive-like rendering shortcut.
        formatSimple(value = value)?.let { formatted: String ->
            return formatted
        }

        // Collection / map / array rendering.
        formatContainer(
            value = value,
            level = level,
            indent = indent,
            printers = printers,
            states = states,
            maxDepth = maxDepth,
        )?.let { formatted: String ->
            return formatted
        }

        // If already registered, either return built result
        // or a stable reference if currently building (cycle).
        val cachedPrinter: (() -> String)? = printers[value]
        if (cachedPrinter != null) {
            val state: NodeState? = states[value]
            if (state == NodeState.Building) {
                return formatReference(
                    value = value,
                )
            }
            return cachedPrinter()
        }

        // Register forward printer before descending into fields.
        // This enables safe cycle handling.
        var builtText: String? = null
        printers[value] = {
            builtText ?: formatReference(value = value)
        }
        states[value] = NodeState.Building

        val objectText: String = formatObject(
            value = value,
            level = level,
            indent = indent,
            printers = printers,
            states = states,
            maxDepth = maxDepth,
        )

        builtText = objectText
        states[value] = NodeState.Built

        return objectText
    }

    /**
     * Formats a non-container object using reflection.
     */
    private fun formatObject(
        value: Any,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        val clazz: Class<*> = value.javaClass
        // Avoid reflection into JDK / Kotlin internals.
        if (shouldUsePlainToString(clazz = clazz)) {
            return value.toString()
        }
        val fields: List<java.lang.reflect.Field> = clazz.declaredFields
            .asSequence()
            .filter { field: java.lang.reflect.Field ->
                !Modifier.isStatic(field.modifiers)
            }
            .toList()
        if (fields.isEmpty()) return value.toString()
        val currentIndent: String = indent.repeat(n = level)
        val nextIndent: String = indent.repeat(n = level + 1)
        return buildString {
            append(clazz.simpleName)
            append("(\n")

            for (field in fields) {
                val fieldText: String = formatField(
                    owner = value,
                    field = field,
                    level = level,
                    indent = indent,
                    printers = printers,
                    states = states,
                    maxDepth = maxDepth,
                )

                append(nextIndent)
                append(field.name)
                append(" = ")
                append(fieldText)
                append(",\n")
            }

            append(currentIndent)
            append(")")
        }
    }

    /**
     * Safely reads and formats a single field.
     * Inaccessible fields are rendered explicitly.
     */
    private fun formatField(
        owner: Any,
        field: java.lang.reflect.Field,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        return try {
            val isAccessible: Boolean = field.trySetAccessible()
            val rawValue: Any? = if (isAccessible) field.get(owner) else "<inaccessible>"
            formatValue(
                value = rawValue,
                level = level + 1,
                indent = indent,
                printers = printers,
                states = states,
                maxDepth = maxDepth,
            )
        } catch (t: Throwable) {
            "<error: ${t::class.simpleName}>"
        }
    }

    /**
     * Renders primitive-like values directly.
     */
    private fun formatSimple(value: Any): String? = when (value) {
        is String -> "\"$value\""
        is Number, is Boolean, is Char -> value.toString()
        is Enum<*> -> value.name
        else -> null
    }

    /**
     * Dispatches to specific container formatters.
     */
    private fun formatContainer(
        value: Any,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String? {
        val currentIndent: String = indent.repeat(n = level)
        val nextIndent: String = indent.repeat(n = level + 1)
        return when (value) {
            is Collection<*> -> formatCollection(
                value = value,
                currentIndent = currentIndent,
                nextIndent = nextIndent,
                level = level,
                indent = indent,
                printers = printers,
                states = states,
                maxDepth = maxDepth,
            )

            is Map<*, *> -> formatMap(
                value = value,
                currentIndent = currentIndent,
                nextIndent = nextIndent,
                level = level,
                indent = indent,
                printers = printers,
                states = states,
                maxDepth = maxDepth,
            )

            is Array<*> -> formatArray(
                value = value,
                currentIndent = currentIndent,
                nextIndent = nextIndent,
                level = level,
                indent = indent,
                printers = printers,
                states = states,
                maxDepth = maxDepth,
            )

            else -> null
        }
    }

    /**
     * Formats a collection as a multi-line list.
     */
    private fun formatCollection(
        value: Collection<*>,
        currentIndent: String,
        nextIndent: String,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        if (value.isEmpty()) return "[]"
        return buildString {
            append("[\n")
            for (item in value) {
                val itemText: String = formatValue(
                    value = item,
                    level = level + 1,
                    indent = indent,
                    printers = printers,
                    states = states,
                    maxDepth = maxDepth,
                )
                append(nextIndent)
                append(itemText)
                append(",\n")
            }
            append(currentIndent)
            append("]")
        }
    }

    /**
     * Formats a map as key -> value pairs.
     */
    private fun formatMap(
        value: Map<*, *>,
        currentIndent: String,
        nextIndent: String,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        if (value.isEmpty()) return "{}"
        return buildString {
            append("{\n")
            for ((key, mapValue) in value) {
                val keyText: String = formatValue(
                    value = key,
                    level = level + 1,
                    indent = indent,
                    printers = printers,
                    states = states,
                    maxDepth = maxDepth,
                )
                val valueText: String = formatValue(
                    value = mapValue,
                    level = level + 1,
                    indent = indent,
                    printers = printers,
                    states = states,
                    maxDepth = maxDepth,
                )
                append(nextIndent)
                append(keyText)
                append(" -> ")
                append(valueText)
                append(",\n")
            }
            append(currentIndent)
            append("}")
        }
    }

    /**
     * Formats an array similarly to collections.
     */
    private fun formatArray(
        value: Array<*>,
        currentIndent: String,
        nextIndent: String,
        level: Int,
        indent: String,
        printers: IdentityHashMap<Any, () -> String>,
        states: IdentityHashMap<Any, NodeState>,
        maxDepth: Int,
    ): String {
        if (value.isEmpty()) return "[]"
        return buildString {
            append("[\n")
            for (item in value) {
                val itemText: String = formatValue(
                    value = item,
                    level = level + 1,
                    indent = indent,
                    printers = printers,
                    states = states,
                    maxDepth = maxDepth,
                )
                append(nextIndent)
                append(itemText)
                append(",\n")
            }
            append(currentIndent)
            append("]")
        }
    }

    /**
     * Applies a global prefix to every output line.
     */
    private fun applyInitialIndent(text: String, initialIndent: String): String {
        if (initialIndent.isEmpty()) return text
        return text
            .lines()
            .joinToString(separator = "\n") { line: String -> initialIndent + line }
    }

    /**
     * Prevents illegal reflective access into core platform classes.
     */
    private fun shouldUsePlainToString(clazz: Class<*>): Boolean {
        val name: String = clazz.name
        return name.startsWith(prefix = "java.")
                || name.startsWith(prefix = "javax.")
                || name.startsWith(prefix = "jdk.")
                || name.startsWith(prefix = "kotlin.")
    }

    /**
     * Produces a stable identity-based reference for cyclic nodes.
     */
    private fun formatReference(value: Any): String {
        val id: String = System.identityHashCode(value).toString(radix = 16)
        return "${value.javaClass.simpleName}@$id"
    }
}

/**
 * Public extension entry point using default formatter.
 */
public fun Any?.toPrettyString(
    indent: String = "  ",
    initialIndent: String = "  ",
    maxDepth: Int = 10,
): String {
    return DefaultPrettyStringFormatter().format(
        value = this,
        indent = indent,
        initialIndent = initialIndent,
        maxDepth = maxDepth,
    )
}
