package org.alexcawl.sockets.client.domain.main

import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.Message
import org.alexcawl.sockets.contract.entity.User
import java.util.UUID

internal enum class MainWorkspaceMode {
    IDLE,
    CHAT,
    MANAGE_CHATS,
    USERS,
    CHANGE_NAME,
    EXIT_CONFIRMATION,
}

internal sealed interface MainState {

    data object Loading : MainState

    data class Content(
        val userId: UUID,
        val userName: String,
        val users: List<User>,
        val chats: List<Chat>,
        val chatMessages: Map<Int, List<Message>>,
        val chatMembers: Map<Int, Set<UUID>>,
        val selectedChatId: Int?,
        val workspaceMode: MainWorkspaceMode,
        val editUserName: String,
        val newChatName: String,
        val messageDraft: String,
    ) : MainState

    data class Error(val cause: Throwable) : MainState
}
