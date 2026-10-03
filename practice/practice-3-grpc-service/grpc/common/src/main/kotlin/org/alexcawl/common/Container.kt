package org.alexcawl.common

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public typealias Provider<T> = () -> T

public interface Container {

    public fun <T> single(creator: Container.() -> T): ReadOnlyProperty<Container, T> {
        return SingleDelegate(creator = creator)
    }

    public fun <T> factory(creator: Container.() -> T): ReadOnlyProperty<Container, Provider<T>> {
        return FactoryDelegate(creator = creator)
    }
}

private class SingleDelegate<T>(
    private val creator: Container.() -> T,
) : ReadOnlyProperty<Container, T> {

    private object Uninitialized

    private var value: Any? = Uninitialized

    override fun getValue(thisRef: Container, property: KProperty<*>): T {
        if (value === Uninitialized) {
            value = thisRef.creator()
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}

private class FactoryDelegate<T>(
    private val creator: Container.() -> T,
) : ReadOnlyProperty<Container, Provider<T>> {

    override fun getValue(thisRef: Container, property: KProperty<*>): Provider<T> {
        return { thisRef.creator() }
    }
}
