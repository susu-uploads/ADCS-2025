package org.alexcawl.sockets.client.ui.login.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.alexcawl.socket_project.client.generated.resources.Res
import org.alexcawl.socket_project.client.generated.resources.connect_action_connect
import org.alexcawl.socket_project.client.generated.resources.connect_host
import org.alexcawl.socket_project.client.generated.resources.connect_port
import org.alexcawl.socket_project.client.generated.resources.connect_title
import org.alexcawl.socket_project.client.generated.resources.connect_user_name
import org.alexcawl.sockets.client.domain.login.OnConnectClickedUiEvent
import org.alexcawl.sockets.client.domain.login.OnHostChangedUiEvent
import org.alexcawl.sockets.client.domain.login.OnPortChangedUiEvent
import org.alexcawl.sockets.client.domain.login.OnUserNameChangedUiEvent
import org.alexcawl.sockets.client.ui.login.LoginUiNews
import org.alexcawl.sockets.client.ui.login.LoginUiState
import org.alexcawl.sockets.client.ui.login.LoginViewModel
import org.alexcawl.sockets.client.ui.login.NavigateToMainScreen
import org.alexcawl.sockets.client.ui.login.ToastLoginUiNews
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMainScreen: (userId: UUID?, userName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loginUiState: LoginUiState by viewModel.state.collectAsState()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.news.collect { news: LoginUiNews ->
            when (news) {
                is NavigateToMainScreen -> onNavigateToMainScreen(news.userId, news.userName)
                is ToastLoginUiNews -> snackbarHostState.showSnackbar(message = news.message)
            }
        }
    }
    LoginScreen(
        uiState = loginUiState,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onHostChange = { host: String ->
            viewModel.dispatch(event = OnHostChangedUiEvent(host = host))
        },
        onPortChange = { port: String ->
            viewModel.dispatch(event = OnPortChangedUiEvent(port = port))
        },
        onUserNameChange = { userName: String ->
            viewModel.dispatch(event = OnUserNameChangedUiEvent(userName = userName))
        },
        onConnectClick = {
            viewModel.dispatch(event = OnConnectClickedUiEvent)
        },
    )
}

@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onHostChange: (String) -> Unit = {},
    onPortChange: (String) -> Unit = {},
    onUserNameChange: (String) -> Unit = {},
    onConnectClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            when (uiState) {
                is LoginUiState.Loading -> LoadingContent()
                is LoginUiState.Content -> Content(
                    uiState = uiState,
                    onHostChange = onHostChange,
                    onPortChange = onPortChange,
                    onUserNameChange = onUserNameChange,
                    onConnectClick = onConnectClick,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Content(
    uiState: LoginUiState.Content,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onConnectClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(resource = Res.string.connect_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.host,
                onValueChange = onHostChange,
                singleLine = true,
                label = {
                    Text(text = stringResource(resource = Res.string.connect_host))
                },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.port,
                onValueChange = onPortChange,
                singleLine = true,
                label = {
                    Text(text = stringResource(resource = Res.string.connect_port))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.userName,
                onValueChange = onUserNameChange,
                singleLine = true,
                label = {
                    Text(text = stringResource(resource = Res.string.connect_user_name))
                },
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = onConnectClick,
            ) {
                Text(text = stringResource(resource = Res.string.connect_action_connect))
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        LoginScreen(
            uiState = LoginUiState.Content(
                host = "127.0.0.1",
                port = "8080",
                userName = "mikhail",
            ),
        )
    }
}
