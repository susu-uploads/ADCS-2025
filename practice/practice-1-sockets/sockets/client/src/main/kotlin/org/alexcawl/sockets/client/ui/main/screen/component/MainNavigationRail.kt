package org.alexcawl.sockets.client.ui.main.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.socket_project.client.generated.resources.Res
import org.alexcawl.socket_project.client.generated.resources.ic_chat
import org.alexcawl.socket_project.client.generated.resources.ic_groups
import org.alexcawl.socket_project.client.generated.resources.ic_logout
import org.alexcawl.sockets.client.ui.main.MainWorkspaceUiState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun MainNavigationRail(
    workspace: MainWorkspaceUiState,
    onManageChatsClick: () -> Unit,
    onUsersClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            MainRailItem(
                title = "Chats",
                icon = Res.drawable.ic_chat,
                isSelected = workspace is MainWorkspaceUiState.ManageChats,
                onClick = onManageChatsClick,
            )
            MainRailItem(
                title = "Users",
                icon = Res.drawable.ic_groups,
                isSelected = workspace is MainWorkspaceUiState.Users,
                onClick = onUsersClick,
            )
            MainRailItem(
                title = "Exit",
                icon = Res.drawable.ic_logout,
                isSelected = workspace is MainWorkspaceUiState.ExitConfirmation,
                onClick = onExitClick,
            )
        }
    }
}

@Composable
private fun MainRailItem(
    title: String,
    icon: DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    NavigationRailItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(resource = icon),
                contentDescription = null,
            )
        },
        label = {
            Text(text = title)
        },
    )
}
