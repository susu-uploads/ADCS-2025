package org.alexcawl.sockets.client.ui.main.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.socket_project.client.generated.resources.Res
import org.alexcawl.socket_project.client.generated.resources.ic_account_circle
import org.alexcawl.socket_project.client.generated.resources.ic_person_edit
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun MainHeaderCard(
    userName: String,
    onChangeUserNameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_account_circle),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onChangeUserNameClick) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_person_edit),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Change username")
            }
        }
    }
}
