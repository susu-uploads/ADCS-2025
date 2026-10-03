package org.alexcawl.sockets.client.domain.main

import org.alexcawl.kotea.DslUpdate
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.*
import java.util.UUID

internal class MainUpdate : DslUpdate<MainState, MainEvent, MainCommand, MainNews>() {

    override fun NextBuilder.update(event: MainEvent) {
        when (event) {
            is MainUiEvent -> onUiEvent(event = event)
            is OnReceiveMessageEvent -> onReceiveMessage(event = event)
            is OnDecodeServerResponseEvent -> onDecodeServerResponse(event = event)
            is OnReceiveClientRequestEvent -> onReceiveClientRequest(event = event)
            is OnEncodeClientRequestEvent -> onEncodeClientRequest(event = event)
            is OnNetworkFailureEvent -> news(ShowMainToast(message = event.cause.message ?: "Network failure, relaunching session"), OpenLoginScreen)
            is OnNetworkDisconnectedEvent -> news(OpenLoginScreen)
        }
    }

    private fun NextBuilder.onUiEvent(event: MainUiEvent) {
        when (event) {
            is OnManageChatsClickedUiEvent -> updateContent { copy(workspaceMode = MainWorkspaceMode.MANAGE_CHATS) }
            is OnUsersClickedUiEvent -> updateContent { copy(workspaceMode = MainWorkspaceMode.USERS) }
            is OnChangeNameClickedUiEvent -> updateContent {
                copy(
                    workspaceMode = MainWorkspaceMode.CHANGE_NAME,
                    editUserName = userName,
                )
            }

            is OnExitClickedUiEvent -> updateContent { copy(workspaceMode = MainWorkspaceMode.EXIT_CONFIRMATION) }
            is OnExitCancelClickedUiEvent -> updateContent { copy(workspaceMode = MainWorkspaceMode.IDLE) }
            is OnExitConfirmClickedUiEvent -> onDisconnect()
            is OnChatClickedUiEvent -> {
                updateContent {
                    copy(
                        selectedChatId = event.chatId,
                        workspaceMode = if (event.chatId == null) MainWorkspaceMode.IDLE else MainWorkspaceMode.CHAT,
                    )
                }
            }

            is OnMessageChangedUiEvent -> updateContent { copy(messageDraft = event.message) }

            is OnSendMessageClickedUiEvent -> {
                val content: MainState.Content = state as? MainState.Content ?: return
                val chatId: Int = content.selectedChatId ?: return
                val message: String = content.messageDraft.trim()
                if (message.isEmpty()) return
                val selectedChat: Chat? = content.chats.firstOrNull { chat: Chat -> chat.id == chatId }
                if (selectedChat?.type == ChatType.READ_ONLY) {
                    news(ShowMainToast(message = "Read-only chat."))
                    return
                }
                commands(
                    MessageSendCommand(
                        userId = content.userId,
                        chatId = chatId,
                        message = message,
                        type = MessageType.TEXT,
                    )
                )
            }

            is OnNewChatNameChangedUiEvent -> updateContent { copy(newChatName = event.chatName) }

            is OnCreateChatClickedUiEvent -> {
                val content: MainState.Content = state as? MainState.Content ?: return
                val chatName: String = content.newChatName.trim()
                if (chatName.isEmpty()) return
                commands(
                    ChatCreateCommand(
                        userId = content.userId,
                        chatName = chatName,
                    )
                )
            }

            is OnJoinChatClickedUiEvent -> {
                val content: MainState.Content = state as? MainState.Content ?: return
                commands(
                    ChatJoinCommand(
                        userId = content.userId,
                        chatId = event.chatId,
                    )
                )
            }

            is OnLeaveChatClickedUiEvent -> {
                val content: MainState.Content = state as? MainState.Content ?: return
                commands(
                    ChatLeaveCommand(
                        userId = content.userId,
                        chatId = event.chatId,
                    )
                )
            }

            is OnUserNameChangedUiEvent -> updateContent { copy(editUserName = event.userName) }

            is OnSaveUserNameClickedUiEvent -> {
                val content: MainState.Content = state as? MainState.Content ?: return
                val userName: String = content.editUserName.trim()
                if (userName.isEmpty() || userName == content.userName) return
                commands(
                    UserChangeNameCommand(
                        userId = content.userId,
                        userName = userName,
                    )
                )
            }
        }
    }

    private fun NextBuilder.onReceiveMessage(event: OnReceiveMessageEvent) {
        commands(DecodeServerResponseCommand(payload = event.payload))
    }

    private fun NextBuilder.onDecodeServerResponse(event: OnDecodeServerResponseEvent) {
        event.request
            .onSuccess { response: ServerResponse ->
                onReceiveServerResponse(response = response)
            }
            .onFailure { exception: Throwable ->
                state {
                    MainState.Error(cause = exception)
                }
                news(ShowMainToast(message = exception.message ?: "Decode failed"))
            }
    }

    private fun NextBuilder.onReceiveServerResponse(response: ServerResponse) {
        when (response) {
            is UserAuthorizeResponse.Success -> {
                state {
                    MainState.Content(
                        userId = response.user.id,
                        userName = response.user.name,
                        users = listOf(response.user),
                        chats = emptyList(),
                        chatMessages = emptyMap(),
                        chatMembers = emptyMap(),
                        selectedChatId = null,
                        workspaceMode = MainWorkspaceMode.IDLE,
                        editUserName = response.user.name,
                        newChatName = "",
                        messageDraft = "",
                    )
                }
                commands(SubscribeOnNetworkMessagesCommand)
            }

            is UserAuthorizeResponse.AlreadyAuthorized -> {
                news(ShowMainToast(message = "User is already authorized."))
            }

            is UserDeauthorizeResponse.Success -> {
                commands(DisconnectNetworkCommand)
            }

            is UserChangeNameResponse.Success -> {
                updateContent {
                    val updatedUsers: List<User> = users.map { user: User ->
                        if (user.id == response.user.id) response.user else user
                    }
                    copy(
                        userName = if (userId == response.user.id) response.user.name else userName,
                        users = updatedUsers,
                        editUserName = if (userId == response.user.id) response.user.name else editUserName,
                    )
                }
            }

            is UserChangeNameResponse.UserNotFound -> {
                news(ShowMainToast(message = "User not found."))
            }

            is ChatCreateResponse.Success -> {
                updateContent {
                    copy(
                        newChatName = "",
                        workspaceMode = MainWorkspaceMode.MANAGE_CHATS,
                    )
                }
            }

            is ChatCreateResponse.UserNotFound -> {
                news(ShowMainToast(message = "User not found."))
            }

            is ChatJoinResponse.Success -> {
                updateContent {
                    val updatedMembers: MutableMap<Int, Set<UUID>> = chatMembers.toMutableMap()
                    val currentMembers: Set<UUID> = updatedMembers[response.chat.id].orEmpty()
                    updatedMembers[response.chat.id] = currentMembers + response.user.id
                    copy(
                        selectedChatId = response.chat.id,
                        workspaceMode = MainWorkspaceMode.CHAT,
                        chatMembers = updatedMembers.toMap(),
                    )
                }
            }

            is ChatJoinResponse.AlreadyInChat -> {
                news(ShowMainToast(message = "Already in chat."))
            }

            is ChatJoinResponse.ChatNotFound -> {
                news(ShowMainToast(message = "Chat not found."))
            }

            is ChatJoinResponse.UserNotFound -> {
                news(ShowMainToast(message = "User not found."))
            }

            is ChatLeaveResponse.Success -> {
                updateContent {
                    val updatedMembers: MutableMap<Int, Set<UUID>> = chatMembers.toMutableMap()
                    val currentMembers: Set<UUID> = updatedMembers[response.chat.id].orEmpty()
                    updatedMembers[response.chat.id] = currentMembers - response.user.id
                    copy(
                        selectedChatId = if (selectedChatId == response.chat.id && userId == response.user.id) null else selectedChatId,
                        workspaceMode = if (selectedChatId == response.chat.id && userId == response.user.id) MainWorkspaceMode.IDLE else workspaceMode,
                        chatMembers = updatedMembers.toMap(),
                    )
                }
            }

            is ChatLeaveResponse.AlreadyNotInChat -> {
                news(ShowMainToast(message = "Already not in chat."))
            }

            is ChatLeaveResponse.ChatNotFound -> {
                news(ShowMainToast(message = "Chat not found."))
            }

            is ChatLeaveResponse.UserNotFound -> {
                news(ShowMainToast(message = "User not found."))
            }

            is MessageSendResponse.Success -> {
                updateContent {
                    val updatedMessages: MutableMap<Int, List<org.alexcawl.sockets.contract.entity.Message>> = chatMessages.toMutableMap()
                    val currentMessages: List<org.alexcawl.sockets.contract.entity.Message> = updatedMessages[response.chat.id].orEmpty()
                    updatedMessages[response.chat.id] = currentMessages + response.message
                    copy(
                        messageDraft = "",
                        chatMessages = updatedMessages.toMap(),
                    )
                }
            }

            is MessageSendResponse.ChatNotFound -> {
                news(ShowMainToast(message = "Chat not found."))
            }

            is MessageSendResponse.UserNotFound -> {
                news(ShowMainToast(message = "User not found."))
            }

            is MessageSendResponse.UserIsNotInChat -> {
                news(ShowMainToast(message = "You are not a member of this chat."))
            }

            is MessageSendResponse.ChatIsReadOnly -> {
                news(ShowMainToast(message = "Chat is read-only."))
            }

            is UpdateUsersResponse -> {
                updateContent {
                    copy(users = response.users)
                }
            }

            is UpdateChatsResponse -> {
                updateContent {
                    val selectedExists: Boolean = selectedChatId?.let { chatId: Int ->
                        response.chats.any { chat: Chat -> chat.id == chatId }
                    } ?: true
                    copy(
                        chats = response.chats,
                        selectedChatId = if (selectedExists) selectedChatId else null,
                        workspaceMode = if (selectedExists) workspaceMode else MainWorkspaceMode.IDLE,
                    )
                }
            }

            is UpdateChatMessagesResponse -> {
                updateContent {
                    copy(
                        chatMessages = chatMessages + (response.chatId to response.messages),
                    )
                }
            }

            is UpdateChatMembersResponse -> {
                updateContent {
                    copy(
                        chatMembers = chatMembers + (response.chatId to response.memberIds.toSet()),
                    )
                }
            }

            is NotAuthorizedResponse -> {
                news(ShowMainToast(message = "Not authorized."))
            }

            is FailureResponse -> {
                news(ShowMainToast(message = response.message))
            }
        }
    }

    private fun NextBuilder.onReceiveClientRequest(event: OnReceiveClientRequestEvent) {
        commands(EncodeClientRequestCommand(request = event.request, lastAck = event.lastAck))
    }

    private fun NextBuilder.onEncodeClientRequest(event: OnEncodeClientRequestEvent) {
        event.payload
            .onSuccess { payload: String ->
                commands(SendNetworkMessageCommand(payload = payload, lastAck = event.lastAck))
            }
            .onFailure { exception: Throwable ->
                news(ShowMainToast(message = exception.message ?: "Encode failed"))
            }
    }

    private fun NextBuilder.onDisconnect() {
        val currentState: MainState = state
        if (currentState is MainState.Content) {
            commands(UserDeauthorizeCommand(userId = currentState.userId))
        } else {
            commands(DisconnectNetworkCommand)
        }
    }

    private fun NextBuilder.updateContent(transform: MainState.Content.() -> MainState.Content) {
        val currentState: MainState = state
        if (currentState is MainState.Content) {
            val updatedState: MainState.Content = transform(currentState)
            state {
                updatedState
            }
        }
    }
}
