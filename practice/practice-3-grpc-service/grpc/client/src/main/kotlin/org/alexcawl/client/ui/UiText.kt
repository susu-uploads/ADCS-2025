package org.alexcawl.client.ui

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

internal sealed interface UiText {

    data class Raw(val value: String) : UiText

    data class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText
}

internal suspend fun UiText.asText(): String {
    return when (this) {
        is UiText.Raw -> value
        is UiText.Resource -> getString(id, *args.toTypedArray())
    }
}
