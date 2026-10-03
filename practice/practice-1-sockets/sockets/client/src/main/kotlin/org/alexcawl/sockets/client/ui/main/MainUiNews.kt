package org.alexcawl.sockets.client.ui.main

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface MainUiNews

@Immutable
internal data class ShowToastMainUiNews(val message: String) : MainUiNews

@Immutable
internal data object NavigateToLoginMainUiNews : MainUiNews
