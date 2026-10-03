package org.alexcawl.sockets.client.domain.main

import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.contract.response.ServerResponse

internal sealed interface MainEvent

internal data class OnReceiveMessageEvent(val payload: String) : MainEvent

internal data class OnDecodeServerResponseEvent(val request: Result<ServerResponse>) : MainEvent

internal data class OnReceiveClientRequestEvent(val request: ClientRequest, val lastAck: Boolean = false) : MainEvent

internal data class OnEncodeClientRequestEvent(val payload: Result<String>, val lastAck: Boolean = false) : MainEvent

internal data class OnNetworkFailureEvent(val cause: Throwable) : MainEvent

internal data object OnNetworkDisconnectedEvent : MainEvent


internal sealed interface MainUiEvent : MainEvent

internal data object OnManageChatsClickedUiEvent : MainUiEvent

internal data object OnUsersClickedUiEvent : MainUiEvent

internal data object OnChangeNameClickedUiEvent : MainUiEvent

internal data object OnExitClickedUiEvent : MainUiEvent

internal data object OnExitConfirmClickedUiEvent : MainUiEvent

internal data object OnExitCancelClickedUiEvent : MainUiEvent

internal data class OnChatClickedUiEvent(val chatId: Int?) : MainUiEvent

internal data class OnMessageChangedUiEvent(val message: String) : MainUiEvent

internal data object OnSendMessageClickedUiEvent : MainUiEvent

internal data class OnNewChatNameChangedUiEvent(val chatName: String) : MainUiEvent

internal data object OnCreateChatClickedUiEvent : MainUiEvent

internal data class OnJoinChatClickedUiEvent(val chatId: Int) : MainUiEvent

internal data class OnLeaveChatClickedUiEvent(val chatId: Int) : MainUiEvent

internal data class OnUserNameChangedUiEvent(val userName: String) : MainUiEvent

internal data object OnSaveUserNameClickedUiEvent : MainUiEvent
