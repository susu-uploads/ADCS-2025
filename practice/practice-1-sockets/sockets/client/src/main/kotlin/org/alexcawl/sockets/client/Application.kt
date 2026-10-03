package org.alexcawl.sockets.client

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.alexcawl.sockets.client.ui.login.LoginViewModel
import org.alexcawl.sockets.client.ui.login.screen.LoginScreen
import org.alexcawl.sockets.client.ui.main.MainViewModel
import org.alexcawl.sockets.client.ui.main.screen.MainScreen
import java.util.UUID

internal class Application(
    private val applicationModule: ApplicationModule,
) {

    private val loginViewModelFactory: (extras: CreationExtras) -> LoginViewModel
        get() = applicationModule.uiModule::loginViewModel

    private val mainViewModelFactory: (extras: CreationExtras) -> MainViewModel
        get() = applicationModule.uiModule::mainViewModel

    fun launch() {
        application {
            MaterialTheme {
                Window(onCloseRequest = ::exitApplication) {
                    val navController: NavHostController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                    ) {
                        composable(route = "login") {
                            LoginScreen(
                                viewModel = viewModel<LoginViewModel> { loginViewModelFactory(this) },
                                onNavigateToMainScreen = { userId: UUID?, userName: String ->
                                    val userIdArgument: String = userId?.toString() ?: "null"
                                    navController.navigate("main/${userIdArgument}?userName=${userName}")
                                },
                            )
                        }
                        composable(
                            route = "main/{userId}?userName={userName}",
                            arguments = listOf(
                                navArgument(name = "userId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument(name = "userName") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                },
                            )
                        ) {
                            MainScreen(
                                viewModel = viewModel<MainViewModel> { mainViewModelFactory(this) },
                                onBackClick = navController::popBackStack,
                            )
                        }
                    }
                }
            }
        }
    }
}
