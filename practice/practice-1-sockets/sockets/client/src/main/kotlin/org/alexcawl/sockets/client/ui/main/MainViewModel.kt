package org.alexcawl.sockets.client.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.ui.KoteaUiStore
import org.alexcawl.kotea.ui.UiMapper
import org.alexcawl.sockets.client.domain.main.MainNews
import org.alexcawl.sockets.client.domain.main.MainState
import org.alexcawl.sockets.client.domain.main.MainWorkspaceMode
import org.alexcawl.sockets.client.domain.main.MainStore
import org.alexcawl.sockets.client.domain.main.MainUiEvent
import org.alexcawl.sockets.client.domain.main.OpenLoginScreen
import org.alexcawl.sockets.client.domain.main.ShowMainToast
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.entity.UserConnectionType
import org.alexcawl.sockets.contract.entity.UserType
import java.util.UUID
import kotlin.coroutines.CoroutineContext

internal class MainViewModel(
    domainStore: MainStore,
    storeContext: CoroutineContext,
) : ViewModel(), Store<MainUiState, MainUiEvent, MainUiNews> by KoteaUiStore(
    initialState = MainUiState.Loading,
    domainStore = domainStore,
    uiStateMapper = uiStateMapper(),
    uiNewsMapper = uiNewsMapper(),
) {
    init {
        viewModelScope.launch {
            withContext(context = storeContext) {
                launchIn(coroutineScope = this)
            }
        }
    }
}

private fun uiStateMapper(): UiMapper<MainState, MainUiState> = UiMapper { state: MainState ->
    when (state) {
        is MainState.Loading -> MainUiState.Loading

        is MainState.Content -> state.asUiState()

        is MainState.Error -> MainUiState.Error(message = state.cause.message ?: "Unknown error")
    }
}

private fun uiNewsMapper(): UiMapper<MainNews, MainUiNews> = UiMapper { news: MainNews ->
    when (news) {
        is ShowMainToast -> ShowToastMainUiNews(message = news.message)
        is OpenLoginScreen -> NavigateToLoginMainUiNews
    }
}

private fun MainState.Content.asUiState(): MainUiState.Content {
    val allChats: List<MainChatItemUiState> = chats.map { chat: Chat ->
        MainChatItemUiState(
            id = chat.id,
            title = chat.name,
            type = chat.type,
            isSelected = chat.id == selectedChatId,
            isMember = chatMembers[chat.id]?.contains(userId) == true,
        )
    }
    val myChats: List<MainChatItemUiState> = allChats.filter { chat: MainChatItemUiState ->
        chat.isMember
    }
    return MainUiState.Content(
        userName = userName,
        myChats = myChats,
        workspace = asWorkspaceUiState(chats = allChats),
    )
}

private fun MainState.Content.asWorkspaceUiState(chats: List<MainChatItemUiState>): MainWorkspaceUiState {
    return when (workspaceMode) {
        MainWorkspaceMode.IDLE -> MainWorkspaceUiState.Idle

        MainWorkspaceMode.CHAT -> {
            val selectedChat: MainChatItemUiState = chats.firstOrNull { chat: MainChatItemUiState ->
                chat.id == selectedChatId
            } ?: return MainWorkspaceUiState.Idle
            val usersById: Map<UUID, User> = users.associateBy(User::id)
            MainWorkspaceUiState.Chat(
                chatId = selectedChat.id,
                chatTitle = selectedChat.title,
                isReadOnly = selectedChat.type == ChatType.READ_ONLY,
                messageDraft = messageDraft,
                messages = chatMessages[selectedChat.id].orEmpty().map { message ->
                    MainMessageItemUiState(
                        id = message.id,
                        authorName = usersById[message.authorId]?.name ?: message.authorId.toString(),
                        text = message.text,
                        isMine = message.authorId == userId,
                    )
                },
            )
        }

        MainWorkspaceMode.MANAGE_CHATS -> MainWorkspaceUiState.ManageChats(
            newChatName = newChatName,
            chats = chats,
        )

        MainWorkspaceMode.USERS -> MainWorkspaceUiState.Users(
            users = users.map { user: User ->
                MainUserItemUiState(
                    id = user.id,
                    name = user.name,
                    isOnline = user.connectionType == UserConnectionType.ONLINE,
                    isSystem = user.type == UserType.SYSTEM,
                )
            }
        )

        MainWorkspaceMode.CHANGE_NAME -> MainWorkspaceUiState.ChangeName(
            currentUserName = userName,
            newUserName = editUserName,
        )

        MainWorkspaceMode.EXIT_CONFIRMATION -> MainWorkspaceUiState.ExitConfirmation(
            userName = userName,
        )
    }
}
