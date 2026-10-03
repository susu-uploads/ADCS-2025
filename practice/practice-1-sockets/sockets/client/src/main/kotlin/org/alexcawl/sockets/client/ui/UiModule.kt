package org.alexcawl.sockets.client.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import org.alexcawl.sockets.client.domain.login.LoginStore
import org.alexcawl.sockets.client.domain.main.MainStore
import org.alexcawl.sockets.client.ui.login.LoginViewModel
import org.alexcawl.sockets.client.ui.main.MainViewModel
import org.alexcawl.sockets.common.Container
import java.util.UUID
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal class UiModule(
    private val loginStore: () -> LoginStore,
    private val mainStore: (userId: UUID?, userName: String?) -> MainStore,
    private val storeContext: CoroutineContext = EmptyCoroutineContext,
) : Container {

    fun loginViewModel(extras: CreationExtras): LoginViewModel {
        val loginStore: LoginStore = loginStore()
        return LoginViewModel(domainStore = loginStore, storeContext = storeContext)
    }

    fun mainViewModel(extras: CreationExtras): MainViewModel {
        val handle: SavedStateHandle = extras.createSavedStateHandle()
        val userId: UUID? = handle.get<String>("userId")
            ?.takeIf { value: String -> value != "null" }
            ?.let(block = UUID::fromString)
        val userName: String? = handle.get<String>("userName")
            ?.takeIf { value: String -> value.isNotBlank() }
        val mainStore: MainStore = mainStore(userId, userName)
        return MainViewModel(domainStore = mainStore, storeContext = storeContext)
    }
}
