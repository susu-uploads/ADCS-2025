package org.alexcawl.client.domain.login

internal sealed interface LoginNews

internal data class OpenMainScreen(
    val host: String,
    val port: Int,
    val userName: String,
) : LoginNews

internal data object ShowInvalidPortToast : LoginNews

internal data object ShowBlankHostOrUserNameToast : LoginNews
