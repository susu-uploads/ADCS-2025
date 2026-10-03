package org.alexcawl.client.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.alexcawl.client.domain.main.*
import org.alexcawl.client.ui.UiText
import org.alexcawl.grpc.client.generated.resources.Res
import org.alexcawl.grpc.client.generated.resources.message_blank_comment_text
import org.alexcawl.grpc.client.generated.resources.message_blank_post_text
import org.alexcawl.kotea.Store
import org.alexcawl.kotea.ui.KoteaUiStore
import org.alexcawl.kotea.ui.UiMapper
import kotlin.coroutines.CoroutineContext

internal class MainViewModel(
    domainStore: MainStore,
    storeContext: CoroutineContext,
) : ViewModel(), Store<MainUiState, MainUiEvent, MainUiNews> by KoteaUiStore(
    initialState = MainUiState.Loading,
    domainStore = domainStore,
    uiStateMapper = uiStateMapper(),
    uiNewsMapper = uiNewsMapper(),
) {
    init {
        viewModelScope.launch {
            withContext(context = storeContext) {
                launchIn(coroutineScope = this)
            }
        }
    }
}

private fun uiStateMapper(): UiMapper<MainState, MainUiState> = UiMapper { state ->
    if (state.userId == null) {
        MainUiState.Loading
    } else {
        MainUiState.Content(
            userId = state.userId,
            userName = state.userName,
            posts = state.posts,
            newPostText = state.newPostText,
            commentDrafts = state.commentDrafts,
            isLoading = state.isLoading,
        )
    }
}

private fun uiNewsMapper(): UiMapper<MainNews, MainUiNews> = UiMapper { news ->
    when (news) {
        OpenLoginScreen -> NavigateToLoginMainUiNews
        is OpenLoginScreenWithToast -> ShowToastMainUiNews(message = UiText.Raw(news.message))
        is ShowMainDynamicToast -> ShowToastMainUiNews(message = UiText.Raw(news.message))
        ShowBlankPostTextToast -> ShowToastMainUiNews(
            message = UiText.Resource(
                id = Res.string.message_blank_post_text,
            ),
        )
        ShowBlankCommentTextToast -> ShowToastMainUiNews(
            message = UiText.Resource(
                id = Res.string.message_blank_comment_text,
            ),
        )
    }
}
