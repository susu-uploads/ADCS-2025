package org.alexcawl.sockets.client.ui.main

import androidx.compose.runtime.Immutable
import org.alexcawl.sockets.contract.entity.ChatType
import java.util.UUID

@Immutable
internal data class MainChatItemUiState(
    val id: Int,
    val title: String,
    val type: ChatType,
    val isSelected: Boolean,
    val isMember: Boolean,
)

@Immutable
internal data class MainMessageItemUiState(
    val id: Int,
    val authorName: String,
    val text: String,
    val isMine: Boolean,
)

@Immutable
internal data class MainUserItemUiState(
    val id: UUID,
    val name: String,
    val isOnline: Boolean,
    val isSystem: Boolean,
)

@Immutable
internal sealed interface MainWorkspaceUiState {

    @Immutable
    data object Idle : MainWorkspaceUiState

    @Immutable
    data class Chat(
        val chatId: Int,
        val chatTitle: String,
        val isReadOnly: Boolean,
        val messageDraft: String,
        val messages: List<MainMessageItemUiState>,
    ) : MainWorkspaceUiState

    @Immutable
    data class ManageChats(
        val newChatName: String,
        val chats: List<MainChatItemUiState>,
    ) : MainWorkspaceUiState

    @Immutable
    data class Users(
        val users: List<MainUserItemUiState>,
    ) : MainWorkspaceUiState

    @Immutable
    data class ChangeName(
        val currentUserName: String,
        val newUserName: String,
    ) : MainWorkspaceUiState

    @Immutable
    data class ExitConfirmation(
        val userName: String,
    ) : MainWorkspaceUiState
}

@Immutable
internal sealed interface MainUiState {

    @Immutable
    data object Loading : MainUiState

    @Immutable
    data class Content(
        val userName: String,
        val myChats: List<MainChatItemUiState>,
        val workspace: MainWorkspaceUiState,
    ) : MainUiState

    @Immutable
    data class Error(val message: String) : MainUiState
}
