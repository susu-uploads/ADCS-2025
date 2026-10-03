package org.alexcawl.client

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.client.ui.encodeNavArg
import org.alexcawl.client.ui.UiText
import org.alexcawl.client.ui.asText
import org.alexcawl.client.ui.login.screen.LoginScreen
import org.alexcawl.client.ui.main.screen.MainScreen
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch
import org.alexcawl.client.ui.login.LoginViewModel
import org.alexcawl.client.ui.main.MainViewModel

internal class Application(
    private val applicationModule: ApplicationModule,
) {

    private val loginViewModelFactory: (extras: CreationExtras) -> LoginViewModel
        get() = applicationModule.uiModule::loginViewModel

    private val mainViewModelFactory: (extras: CreationExtras) -> MainViewModel
        get() = applicationModule.uiModule::mainViewModel

    private companion object {
        const val LOGIN_ROUTE = "login"
        const val MAIN_ROUTE = "main?host={host}&port={port}&userName={userName}"
        const val ARG_HOST = "host"
        const val ARG_PORT = "port"
        const val ARG_USER_NAME = "userName"
    }

    fun launch() {
        application {
            MaterialTheme {
                val appTitle: String = stringResource(Res.string.app_title)
                Window(
                    onCloseRequest = ::exitApplication,
                    title = appTitle,
                ) {
                    val navController: NavHostController = rememberNavController()
                    val snackbarHostState: SnackbarHostState = remember(::SnackbarHostState)
                    val coroutineScope: CoroutineScope = rememberCoroutineScope()
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                    ) { paddingValues: PaddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = LOGIN_ROUTE,
                            modifier = Modifier.padding(paddingValues = paddingValues)
                        ) {
                            composable(route = LOGIN_ROUTE) {
                                LoginScreen(
                                    viewModel = viewModel<LoginViewModel>(initializer = loginViewModelFactory),
                                    onShowMessage = { message: UiText ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message = message.asText())
                                        }
                                    },
                                    onAuthorized = { host: String, port: Int, userName: String ->
                                        navController.navigate(route = mainRoute(host = host, port = port, userName = userName)) {
                                            popUpTo(LOGIN_ROUTE) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                )
                            }
                            composable(
                                route = MAIN_ROUTE,
                                arguments = listOf(
                                    navArgument(name = ARG_HOST) { type = NavType.StringType },
                                    navArgument(name = ARG_PORT) { type = NavType.IntType },
                                    navArgument(name = ARG_USER_NAME) { type = NavType.StringType },
                                ),
                            ) {
                                MainScreen(
                                    viewModel = viewModel<MainViewModel>(initializer = mainViewModelFactory),
                                    onShowMessage = { message: UiText ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message = message.asText())
                                        }
                                    },
                                    onLogout = {
                                        navController.navigate(route = loginRoute()) {
                                            popUpTo(MAIN_ROUTE) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mainRoute(host: String, port: Int, userName: String): String {
        return "main?host=${host.encodeNavArg()}&port=$port&userName=${userName.encodeNavArg()}"
    }

    private fun loginRoute(): String {
        return LOGIN_ROUTE
    }
}
