package org.alexcawl.client.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MainNavigationRail(
    selected: MainDestination,
    onDestinationClick: (MainDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .width(96.dp),
        header = {},
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MainDestination.entries.forEach { destination ->
                val title: String = stringResource(destination.title)
                NavigationRailItem(
                    selected = destination == selected,
                    onClick = { onDestinationClick(destination) },
                    icon = {
                        Icon(
                            imageVector = when (destination) {
                                MainDestination.FEED -> Icons.AutoMirrored.Filled.Feed
                                MainDestination.NEW_POST -> Icons.Filled.Edit
                            },
                            contentDescription = title,
                        )
                    },
                    label = { Text(title) },
                )
            }
        }
    }
}
