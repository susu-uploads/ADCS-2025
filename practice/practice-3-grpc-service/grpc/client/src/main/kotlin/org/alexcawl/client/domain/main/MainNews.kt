package org.alexcawl.client.domain.main

internal sealed interface MainNews

internal data class ShowMainDynamicToast(
    val message: String,
) : MainNews

internal data object ShowBlankPostTextToast : MainNews

internal data object ShowBlankCommentTextToast : MainNews

internal data object OpenLoginScreen : MainNews

internal data class OpenLoginScreenWithToast(
    val message: String,
) : MainNews
