package org.alexcawl.sockets.client.ui.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.sockets.client.domain.main.OnChangeNameClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnChatClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnCreateChatClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnExitCancelClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnExitClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnExitConfirmClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnJoinChatClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnLeaveChatClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnManageChatsClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnMessageChangedUiEvent
import org.alexcawl.sockets.client.domain.main.OnNewChatNameChangedUiEvent
import org.alexcawl.sockets.client.domain.main.OnSaveUserNameClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnSendMessageClickedUiEvent
import org.alexcawl.sockets.client.domain.main.OnUserNameChangedUiEvent
import org.alexcawl.sockets.client.domain.main.OnUsersClickedUiEvent
import org.alexcawl.sockets.client.ui.main.MainUiState
import org.alexcawl.sockets.client.ui.main.MainViewModel
import org.alexcawl.sockets.client.ui.main.NavigateToLoginMainUiNews
import org.alexcawl.sockets.client.ui.main.ShowToastMainUiNews
import org.alexcawl.sockets.client.ui.main.screen.component.MainChatsPanel
import org.alexcawl.sockets.client.ui.main.screen.component.MainHeaderCard
import org.alexcawl.sockets.client.ui.main.screen.component.MainNavigationRail
import org.alexcawl.sockets.client.ui.main.screen.component.MainWorkspace

@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState: MainUiState by viewModel.state.collectAsState()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.news.collect { news ->
            when (news) {
                is NavigateToLoginMainUiNews -> onBackClick()
                is ShowToastMainUiNews -> snackbarHostState.showSnackbar(message = news.message)
            }
        }
    }
    MainScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onChangeUserNameClick = {
            viewModel.dispatch(event = OnChangeNameClickedUiEvent)
        },
        onChatClick = { chatId: Int ->
            viewModel.dispatch(event = OnChatClickedUiEvent(chatId = chatId))
        },
        onManageChatsClick = {
            viewModel.dispatch(event = OnManageChatsClickedUiEvent)
        },
        onUsersClick = {
            viewModel.dispatch(event = OnUsersClickedUiEvent)
        },
        onExitClick = {
            viewModel.dispatch(event = OnExitClickedUiEvent)
        },
        onMessageChange = { message: String ->
            viewModel.dispatch(event = OnMessageChangedUiEvent(message = message))
        },
        onSendMessageClick = {
            viewModel.dispatch(event = OnSendMessageClickedUiEvent)
        },
        onNewChatNameChange = { chatName: String ->
            viewModel.dispatch(event = OnNewChatNameChangedUiEvent(chatName = chatName))
        },
        onCreateChatClick = {
            viewModel.dispatch(event = OnCreateChatClickedUiEvent)
        },
        onJoinChatClick = { chatId: Int ->
            viewModel.dispatch(event = OnJoinChatClickedUiEvent(chatId = chatId))
        },
        onLeaveChatClick = { chatId: Int ->
            viewModel.dispatch(event = OnLeaveChatClickedUiEvent(chatId = chatId))
        },
        onUserNameChange = { userName: String ->
            viewModel.dispatch(event = OnUserNameChangedUiEvent(userName = userName))
        },
        onSaveUserNameClick = {
            viewModel.dispatch(event = OnSaveUserNameClickedUiEvent)
        },
        onExitConfirmClick = {
            viewModel.dispatch(event = OnExitConfirmClickedUiEvent)
        },
        onExitCancelClick = {
            viewModel.dispatch(event = OnExitCancelClickedUiEvent)
        },
    )
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    snackbarHostState: SnackbarHostState,
    onChangeUserNameClick: () -> Unit,
    onChatClick: (Int) -> Unit,
    onManageChatsClick: () -> Unit,
    onUsersClick: () -> Unit,
    onExitClick: () -> Unit,
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        when (uiState) {
            is MainUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }

            is MainUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = uiState.message, style = MaterialTheme.typography.bodyLarge)
                }
            }

            is MainUiState.Content -> {
                Content(
                    uiState = uiState,
                    onChangeUserNameClick = onChangeUserNameClick,
                    onChatClick = onChatClick,
                    onManageChatsClick = onManageChatsClick,
                    onUsersClick = onUsersClick,
                    onExitClick = onExitClick,
                    onMessageChange = onMessageChange,
                    onSendMessageClick = onSendMessageClick,
                    onNewChatNameChange = onNewChatNameChange,
                    onCreateChatClick = onCreateChatClick,
                    onJoinChatClick = onJoinChatClick,
                    onLeaveChatClick = onLeaveChatClick,
                    onUserNameChange = onUserNameChange,
                    onSaveUserNameClick = onSaveUserNameClick,
                    onExitConfirmClick = onExitConfirmClick,
                    onExitCancelClick = onExitCancelClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun Content(
    uiState: MainUiState.Content,
    onChangeUserNameClick: () -> Unit,
    onChatClick: (Int) -> Unit,
    onManageChatsClick: () -> Unit,
    onUsersClick: () -> Unit,
    onExitClick: () -> Unit,
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MainHeaderCard(
            userName = uiState.userName,
            onChangeUserNameClick = onChangeUserNameClick,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            MainChatsPanel(
                chats = uiState.myChats,
                onChatClick = onChatClick,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            MainWorkspace(
                workspace = uiState.workspace,
                onMessageChange = onMessageChange,
                onSendMessageClick = onSendMessageClick,
                onNewChatNameChange = onNewChatNameChange,
                onCreateChatClick = onCreateChatClick,
                onJoinChatClick = onJoinChatClick,
                onLeaveChatClick = onLeaveChatClick,
                onUserNameChange = onUserNameChange,
                onSaveUserNameClick = onSaveUserNameClick,
                onExitConfirmClick = onExitConfirmClick,
                onExitCancelClick = onExitCancelClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            MainNavigationRail(
                workspace = uiState.workspace,
                onManageChatsClick = onManageChatsClick,
                onUsersClick = onUsersClick,
                onExitClick = onExitClick,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    }
}
