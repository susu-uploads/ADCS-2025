package org.alexcawl.client.ui.component

import org.alexcawl.grpc.client.generated.resources.*
import org.jetbrains.compose.resources.StringResource

internal enum class MainDestination(
    val route: String,
    val title: StringResource,
) {
    FEED(route = "feed", title = Res.string.nav_feed),
    NEW_POST(route = "new-post", title = Res.string.nav_new_post),
}
