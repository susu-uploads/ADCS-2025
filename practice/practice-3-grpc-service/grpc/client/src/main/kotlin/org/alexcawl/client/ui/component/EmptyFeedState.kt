package org.alexcawl.client.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.grpc.client.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyFeedState(
    modifier: Modifier = Modifier,
) {
    val emptyText: String = stringResource(Res.string.empty_feed)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = emptyText,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
