package org.alexcawl.client.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import org.alexcawl.client.domain.login.LoginStore
import org.alexcawl.client.domain.main.MainStore
import org.alexcawl.client.network.NetworkModule
import org.alexcawl.client.ui.decodeNavArg
import org.alexcawl.client.ui.login.LoginViewModel
import org.alexcawl.client.ui.main.MainViewModel
import org.alexcawl.common.Container
import kotlin.coroutines.CoroutineContext

internal class UiModule(
    private val loginStore: () -> LoginStore,
    private val mainStore: (userName: String, networkModule: NetworkModule) -> MainStore,
    private val networkModule: (host: String, port: Int) -> NetworkModule,
    private val storeContext: CoroutineContext,
) : Container {

    fun loginViewModel(extras: CreationExtras): LoginViewModel {
        return LoginViewModel(
            domainStore = loginStore(),
            storeContext = storeContext,
        )
    }

    fun mainViewModel(extras: CreationExtras): MainViewModel {
        val handle: SavedStateHandle = extras.createSavedStateHandle()
        val host: String = handle.get<String>("host")
            ?.takeIf(String::isNotBlank)
            ?.decodeNavArg()
            ?: error("Missing host nav argument")
        val port: Int = handle.get<Int>("port")
            ?: error("Missing port nav argument")
        val userName: String = handle.get<String>("userName")
            ?.takeIf(String::isNotBlank)
            ?.decodeNavArg()
            ?: error("Missing userName nav argument")
        val networkModule: NetworkModule = networkModule(host, port)
        return MainViewModel(
            domainStore = mainStore(userName, networkModule),
            storeContext = storeContext,
        ).also { mainViewModel: MainViewModel ->
            mainViewModel.addCloseable(closeable = networkModule)
        }
    }
}
