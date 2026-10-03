package org.alexcawl.sockets.client.ui.main.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.alexcawl.socket_project.client.generated.resources.Res
import org.alexcawl.socket_project.client.generated.resources.ic_account_circle
import org.alexcawl.socket_project.client.generated.resources.ic_settings
import org.alexcawl.sockets.client.ui.main.MainUserItemUiState
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun MainUserItem(
    user: MainUserItemUiState,
    modifier: Modifier = Modifier,
) {
    val iconColor: Color = if (user.isOnline) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
    val iconRes = if (user.isSystem) Res.drawable.ic_settings else Res.drawable.ic_account_circle
    val containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(resource = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor,
            )
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (user.isOnline) "online" else "offline",
                style = MaterialTheme.typography.labelSmall,
                color = iconColor,
            )
        }
    }
}
