package org.alexcawl.client.ui.main

import org.alexcawl.client.ui.UiText

internal sealed interface MainUiNews

internal data class ShowToastMainUiNews(val message: UiText) : MainUiNews

internal data object NavigateToLoginMainUiNews : MainUiNews
