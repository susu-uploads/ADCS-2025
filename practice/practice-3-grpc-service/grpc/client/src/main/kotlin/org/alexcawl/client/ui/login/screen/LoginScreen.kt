package org.alexcawl.client.ui.login.screen

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
import androidx.compose.ui.unit.dp
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.client.domain.login.OnAuthorizeClickedUiEvent
import org.alexcawl.client.domain.login.OnHostChangedUiEvent
import org.alexcawl.client.domain.login.OnPortChangedUiEvent
import org.alexcawl.client.domain.login.OnUserNameChangedUiEvent
import org.alexcawl.client.ui.UiText
import org.alexcawl.client.ui.login.LoginUiNews
import org.alexcawl.client.ui.login.LoginUiState
import org.alexcawl.client.ui.login.LoginViewModel
import org.alexcawl.client.ui.login.NavigateToMainScreen
import org.alexcawl.client.ui.login.ShowToastLoginUiNews
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthorized: (host: String, port: Int, userName: String) -> Unit,
    onShowMessage: (message: UiText) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState: LoginUiState by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.news.collect { news: LoginUiNews ->
            when (news) {
                is NavigateToMainScreen -> onAuthorized(news.host, news.port, news.userName)
                is ShowToastLoginUiNews -> onShowMessage(news.message)
            }
        }
    }
    LoginScreen(
        uiState = uiState,
        modifier = modifier,
        onHostChange = { viewModel.dispatch(OnHostChangedUiEvent(host = it)) },
        onPortChange = { viewModel.dispatch(OnPortChangedUiEvent(port = it)) },
        onUserNameChange = { viewModel.dispatch(OnUserNameChangedUiEvent(userName = it)) },
        onAuthorizeClick = { viewModel.dispatch(OnAuthorizeClickedUiEvent) },
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onAuthorizeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState) {
            LoginUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is LoginUiState.Content -> Content(
                uiState = uiState,
                onHostChange = onHostChange,
                onPortChange = onPortChange,
                onUserNameChange = onUserNameChange,
                onAuthorizeClick = onAuthorizeClick,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: LoginUiState.Content,
    onHostChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onAuthorizeClick: () -> Unit,
) {
    val title: String = stringResource(Res.string.login_title)
    val hostLabel: String = stringResource(Res.string.field_host)
    val portLabel: String = stringResource(Res.string.field_port)
    val userNameLabel: String = stringResource(Res.string.field_user_name)
    val authorizeText: String = stringResource(Res.string.action_authorize)
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
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.host,
                onValueChange = onHostChange,
                singleLine = true,
                label = { Text(hostLabel) },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.port,
                onValueChange = onPortChange,
                singleLine = true,
                label = { Text(portLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.userName,
                onValueChange = onUserNameChange,
                singleLine = true,
                label = { Text(userNameLabel) },
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = onAuthorizeClick,
            ) {
                Text(authorizeText)
            }
        }
    }
}
