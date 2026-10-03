package org.alexcawl.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromStringMap
import java.io.InputStream
import java.util.Properties as JavaProperties

public fun resourcesProperties(name: String): JavaProperties {
    val properties = JavaProperties()
    properties.resourcesProperties(name = name)
    return properties
}

public fun JavaProperties.resourcesProperties(name: String) {
    val classLoader: ClassLoader? = Thread.currentThread().contextClassLoader
        ?: this::class.java.classLoader
    val inputStream: InputStream? = classLoader?.getResourceAsStream(name)
    requireNotNull(inputStream) { "Resource '$name' not found on classpath" }
    inputStream.use(this::load)
}

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T> JavaProperties.decode(): T {
    val map: Map<String, String> = entries.associate { (key: Any?, value: Any?) ->
        key.toString() to value.toString()
    }
    return Properties.decodeFromStringMap(map = map)
}
