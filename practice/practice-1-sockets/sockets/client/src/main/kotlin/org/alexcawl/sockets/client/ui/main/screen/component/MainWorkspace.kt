package org.alexcawl.sockets.client.ui.main.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.socket_project.client.generated.resources.Res
import org.alexcawl.socket_project.client.generated.resources.ic_add_chat
import org.alexcawl.socket_project.client.generated.resources.ic_join_chat
import org.alexcawl.socket_project.client.generated.resources.ic_leave_chat
import org.alexcawl.socket_project.client.generated.resources.ic_send
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.client.ui.main.MainChatItemUiState
import org.alexcawl.sockets.client.ui.main.MainWorkspaceUiState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun MainWorkspace(
    workspace: MainWorkspaceUiState,
    onMessageChange: (String) -> Unit,
    onSendMessageClick: () -> Unit,
    onNewChatNameChange: (String) -> Unit,
    onCreateChatClick: () -> Unit,
    onJoinChatClick: (Int) -> Unit,
    onLeaveChatClick: (Int) -> Unit,
    onUserNameChange: (String) -> Unit,
    onSaveUserNameClick: () -> Unit,
    onExitConfirmClick: () -> Unit,
    onExitCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(12.dp),
    ) {
        when (workspace) {
            is MainWorkspaceUiState.Idle -> IdleWorkspace()
            is MainWorkspaceUiState.Chat -> ChatWorkspace(
                workspace = workspace,
                onMessageChange = onMessageChange,
                onSendMessageClick = onSendMessageClick,
            )

            is MainWorkspaceUiState.ManageChats -> ManageChatsWorkspace(
                workspace = workspace,
                onNewChatNameChange = onNewChatNameChange,
                onCreateChatClick = onCreateChatClick,
                onJoinChatClick = onJoinChatClick,
                onLeaveChatClick = onLeaveChatClick,
            )

            is MainWorkspaceUiState.Users -> UsersWorkspace(workspace = workspace)
            is MainWorkspaceUiState.ChangeName -> ChangeNameWorkspace(
                workspace = workspace,
                onUserNameChange = onUserNameChange,
                onSaveUserNameClick = onSaveUserNameClick,
            )

            is MainWorkspaceUiState.ExitConfirmation -> ExitConfirmationWorkspace(
                workspace = workspace,
                onExitConfirmClick = onExitConfirmClick,
                onExitCancelClick = onExitCancelClick,
            )
        }
    }
}

@Composable
private fun IdleWorkspace() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Select a chat or workspace",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ChatWorkspace(
    workspace: MainWorkspaceUiState.Chat,
    onMessageChange: (String) -> Unit,
    onSendMessageClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = workspace.chatTitle,
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = workspace.messages, key = { message -> message.id }) { message ->
                MainMessageItem(message = message)
            }
        }
        if (workspace.isReadOnly) {
            Text(
                text = "Read-only chat",
                style = MaterialTheme.typography.labelLarge,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = workspace.messageDraft,
                    onValueChange = onMessageChange,
                    singleLine = true,
                    label = {
                        Text("Message")
                    },
                )
                Button(onClick = onSendMessageClick) {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_send),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun ManageChatsWorkspace(
    workspace: MainWorkspaceUiState.ManageChats,
    onNewChatNameChange: (String) -> Unit,
    onCreateChatClick: () -> Unit,
    onJoinChatClick: (Int) -> Unit,
    onLeaveChatClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = workspace.newChatName,
                onValueChange = onNewChatNameChange,
                singleLine = true,
                label = {
                    Text("New chat")
                },
            )
            Button(
                onClick = onCreateChatClick,
                modifier = Modifier.height(IntrinsicSize.Min),
            ) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_add_chat),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text("Create")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = workspace.chats, key = { chat: MainChatItemUiState -> chat.id }) { chat: MainChatItemUiState ->
                ManageChatItem(
                    chat = chat,
                    onJoinChatClick = onJoinChatClick,
                    onLeaveChatClick = onLeaveChatClick,
                )
            }
        }
    }
}

@Composable
private fun ManageChatItem(
    chat: MainChatItemUiState,
    onJoinChatClick: (Int) -> Unit,
    onLeaveChatClick: (Int) -> Unit,
) {
    val actionText: String?
    val actionIcon: DrawableResource?
    val onActionClick: (() -> Unit)?
    if (chat.type == ChatType.READ_ONLY) {
        actionText = null
        actionIcon = null
        onActionClick = null
    } else if (chat.isMember) {
        actionText = "Leave"
        actionIcon = Res.drawable.ic_leave_chat
        onActionClick = { onLeaveChatClick(chat.id) }
    } else {
        actionText = "Join"
        actionIcon = Res.drawable.ic_join_chat
        onActionClick = { onJoinChatClick(chat.id) }
    }

    MainManageChatItem(
        title = chat.title,
        actionText = actionText,
        actionIcon = actionIcon,
        onActionClick = onActionClick,
    )
}

@Composable
private fun UsersWorkspace(workspace: MainWorkspaceUiState.Users) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = workspace.users, key = { user -> user.id }) { user ->
            MainUserItem(user = user)
        }
    }
}

@Composable
private fun ChangeNameWorkspace(
    workspace: MainWorkspaceUiState.ChangeName,
    onUserNameChange: (String) -> Unit,
    onSaveUserNameClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Current: ${workspace.currentUserName}",
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = workspace.newUserName,
                onValueChange = onUserNameChange,
                singleLine = true,
                label = {
                    Text("New username")
                },
            )
            Button(onClick = onSaveUserNameClick) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun ExitConfirmationWorkspace(
    workspace: MainWorkspaceUiState.ExitConfirmation,
    onExitConfirmClick: () -> Unit,
    onExitCancelClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Disconnect ${workspace.userName}?",
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onExitConfirmClick) {
                    Text("Disconnect")
                }
                Button(onClick = onExitCancelClick) {
                    Text("Cancel")
                }
            }
        }
    }
}
