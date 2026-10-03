package org.alexcawl.sockets.client.ui.login

import androidx.compose.runtime.Stable
import java.util.UUID

@Stable
sealed interface LoginUiNews

@Stable
sealed interface ToastLoginUiNews : LoginUiNews {
    val message: String
}

@Stable
data class NavigateToMainScreen(val userId: UUID?, val userName: String) : LoginUiNews

@Stable
data class ShowToast(override val message: String) : ToastLoginUiNews
