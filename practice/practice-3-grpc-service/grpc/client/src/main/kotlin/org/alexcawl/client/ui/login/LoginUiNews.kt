package org.alexcawl.client.ui.login

import org.alexcawl.client.ui.UiText

internal sealed interface LoginUiNews

internal data class NavigateToMainScreen(
    val host: String,
    val port: Int,
    val userName: String,
) : LoginUiNews

internal data class ShowToastLoginUiNews(
    val message: UiText,
) : LoginUiNews
