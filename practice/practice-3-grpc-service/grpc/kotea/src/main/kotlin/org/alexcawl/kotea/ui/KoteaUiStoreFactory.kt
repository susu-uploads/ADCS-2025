package org.alexcawl.kotea.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.alexcawl.kotea.Store

public fun <State, UiState, UiEvent, News, UiNews> KoteaUiStore(
    initialState: UiState,
    domainStore: Store<State, UiEvent, News>,
    uiStateMapper: UiMapper<State, UiState>,
    uiNewsMapper: UiMapper<News, UiNews>,
): Store<UiState, UiEvent, UiNews> {
    return UiStoreImpl(
        initialUiState = initialState,
        domainStore = domainStore,
        uiStateMapper = uiStateMapper,
        uiNewsMapper = uiNewsMapper,
    )
}

private class UiStoreImpl<State, UiState, UiEvent, News, UiNews>(
    initialUiState: UiState,
    private val domainStore: Store<State, UiEvent, News>,
    private val uiStateMapper: UiMapper<State, UiState>,
    private val uiNewsMapper: UiMapper<News, UiNews>,
) : Store<UiState, UiEvent, UiNews> {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(value = initialUiState)

    override val state: StateFlow<UiState> = _state.asStateFlow()

    private val _news: MutableSharedFlow<UiNews> = MutableSharedFlow()

    override val news: Flow<UiNews> = _news.asSharedFlow()

    override fun dispatch(event: UiEvent): Unit = domainStore.dispatch(event = event)

    override fun launchIn(coroutineScope: CoroutineScope): Job {
        return coroutineScope.launch {
            domainStore.launchIn(coroutineScope = this)
            launch {
                domainStore.state
                    .map(transform = uiStateMapper::mapUi)
                    .collect { uiState -> _state.emit(value = uiState) }
            }
            launch {
                domainStore.news
                    .map(transform = uiNewsMapper::mapUi)
                    .collect { news -> _news.emit(value = news) }
            }
        }
    }
}
