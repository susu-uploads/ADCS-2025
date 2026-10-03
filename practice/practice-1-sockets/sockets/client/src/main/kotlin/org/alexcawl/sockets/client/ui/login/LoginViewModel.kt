package org.alexcawl.sockets.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.ui.KoteaUiStore
import org.alexcawl.kotea.ui.UiMapper
import org.alexcawl.sockets.client.domain.login.LoginNews
import org.alexcawl.sockets.client.domain.login.LoginState
import org.alexcawl.sockets.client.domain.login.LoginStore
import org.alexcawl.sockets.client.domain.login.LoginUiEvent
import org.alexcawl.sockets.client.domain.login.OpenMainScreen
import org.alexcawl.sockets.client.domain.login.ShowConnectionFailedToast
import org.alexcawl.sockets.client.domain.login.ShowPortInvalidToast
import kotlin.coroutines.CoroutineContext

internal class LoginViewModel(
    domainStore: LoginStore,
    storeContext: CoroutineContext,
) : ViewModel(), Store<LoginUiState, LoginUiEvent, LoginUiNews> by KoteaUiStore(
    initialState = LoginUiState.Loading,
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

private fun uiStateMapper(): UiMapper<LoginState, LoginUiState> = UiMapper { state: LoginState ->
    LoginUiState.Content(
        host = state.host,
        port = state.port.toString(),
        userName = state.userName,
    )
}

private fun uiNewsMapper(): UiMapper<LoginNews, LoginUiNews> = UiMapper { news: LoginNews ->
    when (news) {
        is OpenMainScreen -> NavigateToMainScreen(userId = null, userName = news.userName)

        is ShowPortInvalidToast -> ShowToast(message = "Port ${news.port} is invalid.")

        is ShowConnectionFailedToast -> ShowToast(message = "Cannot connect to socket.")
    }
}
