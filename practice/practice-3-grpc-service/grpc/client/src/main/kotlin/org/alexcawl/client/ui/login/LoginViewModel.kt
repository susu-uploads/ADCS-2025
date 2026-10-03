package org.alexcawl.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.alexcawl.client.domain.login.LoginNews
import org.alexcawl.client.domain.login.LoginState
import org.alexcawl.client.domain.login.LoginStore
import org.alexcawl.client.domain.login.LoginUiEvent
import org.alexcawl.client.domain.login.OpenMainScreen
import org.alexcawl.client.domain.login.ShowBlankHostOrUserNameToast
import org.alexcawl.client.domain.login.ShowInvalidPortToast
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.ui.KoteaUiStore
import org.alexcawl.kotea.ui.UiMapper
import org.alexcawl.client.ui.UiText
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

private fun uiStateMapper(): UiMapper<LoginState, LoginUiState> = UiMapper { state ->
    LoginUiState.Content(
        host = state.host,
        port = state.port,
        userName = state.userName,
    )
}

private fun uiNewsMapper(): UiMapper<LoginNews, LoginUiNews> = UiMapper { news ->
    when (news) {
        is OpenMainScreen -> NavigateToMainScreen(
            host = news.host,
            port = news.port,
            userName = news.userName,
        )
        ShowInvalidPortToast -> ShowToastLoginUiNews(
            message = UiText.Resource(
                id = Res.string.message_invalid_port,
            ),
        )
        ShowBlankHostOrUserNameToast -> ShowToastLoginUiNews(
            message = UiText.Resource(
                id = Res.string.message_blank_host_or_user_name,
            ),
        )
    }
}
