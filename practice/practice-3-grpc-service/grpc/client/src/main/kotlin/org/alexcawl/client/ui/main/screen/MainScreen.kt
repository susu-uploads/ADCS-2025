package org.alexcawl.client.ui.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.alexcawl.client.domain.main.MainUiEvent
import org.alexcawl.client.domain.main.OnCommentClickedUiEvent
import org.alexcawl.client.domain.main.OnCommentTextChangedUiEvent
import org.alexcawl.client.domain.main.OnCreatePostClickedUiEvent
import org.alexcawl.client.domain.main.OnDislikeClickedUiEvent
import org.alexcawl.client.domain.main.OnLikeClickedUiEvent
import org.alexcawl.client.domain.main.OnLogoutClickedUiEvent
import org.alexcawl.client.domain.main.OnNewPostTextChangedUiEvent
import org.alexcawl.client.domain.main.OnRefreshClickedUiEvent
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.client.ui.component.AppHeader
import org.alexcawl.client.ui.component.CommentPanel
import org.alexcawl.client.ui.component.EmptyFeedState
import org.alexcawl.client.ui.component.FeedPostCard
import org.alexcawl.client.ui.component.MainDestination
import org.alexcawl.client.ui.component.MainNavigationRail
import org.alexcawl.client.ui.component.NewPostComposer
import org.alexcawl.client.ui.UiText
import org.alexcawl.client.ui.main.MainUiNews
import org.alexcawl.client.ui.main.MainUiState
import org.alexcawl.client.ui.main.MainViewModel
import org.alexcawl.client.ui.main.NavigateToLoginMainUiNews
import org.alexcawl.client.ui.main.ShowToastMainUiNews
import org.alexcawl.contract.entity.Post
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    onShowMessage: (message: UiText) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState: MainUiState by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.news.collect { news: MainUiNews ->
            when (news) {
                NavigateToLoginMainUiNews -> onLogout()
                is ShowToastMainUiNews -> onShowMessage(news.message)
            }
        }
    }
    MainScreen(
        uiState = uiState,
        modifier = modifier,
        dispatch = viewModel::dispatch,
    )
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    dispatch: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState) {
            MainUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MainUiState.Content -> Content(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                onRefreshClick = { dispatch(OnRefreshClickedUiEvent) },
                onLogoutClick = { dispatch(OnLogoutClickedUiEvent) },
                onNewPostTextChange = { dispatch(OnNewPostTextChangedUiEvent(text = it)) },
                onCreatePostClick = { dispatch(OnCreatePostClickedUiEvent) },
                onLikeToggleClick = { postId, isLiked ->
                    dispatch(
                        if (isLiked) OnDislikeClickedUiEvent(postId = postId)
                        else OnLikeClickedUiEvent(postId = postId),
                    )
                },
                onCommentTextChange = { postId, text ->
                    dispatch(OnCommentTextChangedUiEvent(postId = postId, text = text))
                },
                onCommentSendClick = { dispatch(OnCommentClickedUiEvent(postId = it)) },
            )
        }
    }
}

@Composable
private fun Content(
    uiState: MainUiState.Content,
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNewPostTextChange: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    onLikeToggleClick: (Long, Boolean) -> Unit,
    onCommentTextChange: (Long, String) -> Unit,
    onCommentSendClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title: String = stringResource(Res.string.feed_title)
    val subtitle: String = stringResource(Res.string.feed_subtitle_authorized_as, uiState.userName)
    val navController = rememberNavController()
    val navEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = MainDestination.entries.firstOrNull { destination ->
        destination.route == navEntry?.destination?.route
    } ?: MainDestination.FEED
    var selectedPostId: Long? by rememberSaveable { mutableStateOf(null) }
    val selectedPost: Post? = uiState.posts.firstOrNull { post -> post.id == selectedPostId }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppHeader(
            title = title,
            subtitle = subtitle,
            onRefreshClick = onRefreshClick,
            onLogoutClick = onLogoutClick,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            MainNavigationRail(
                selected = selectedDestination,
                onDestinationClick = { destination ->
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxHeight(),
            )
            NavHost(
                navController = navController,
                startDestination = MainDestination.FEED.route,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
            ) {
                composable(route = MainDestination.FEED.route) {
                    FeedRoute(
                        posts = uiState.posts,
                        currentUserId = uiState.userId,
                        selectedPostId = selectedPostId,
                        onLikeToggleClick = onLikeToggleClick,
                        onCommentClick = { postId ->
                            selectedPostId = if (selectedPostId == postId) {
                                null
                            } else {
                                postId
                            }
                        },
                        isLoading = uiState.isLoading,
                    )
                }
                composable(route = MainDestination.NEW_POST.route) {
                    NewPostRoute(
                        userName = uiState.userName,
                        newPostText = uiState.newPostText,
                        isLoading = uiState.isLoading,
                        onTextChange = onNewPostTextChange,
                        onCreatePostClick = onCreatePostClick,
                    )
                }
            }
            if (selectedDestination == MainDestination.FEED && selectedPost != null) {
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                CommentPanel(
                    post = selectedPost,
                    draft = uiState.commentDrafts[selectedPost.id].orEmpty(),
                    onDraftChange = { onCommentTextChange(selectedPost.id, it) },
                    onSendClick = { onCommentSendClick(selectedPost.id) },
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                        .padding(start = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedRoute(
    posts: List<Post>,
    currentUserId: String,
    selectedPostId: Long?,
    onLikeToggleClick: (Long, Boolean) -> Unit,
    onCommentClick: (Long) -> Unit,
    isLoading: Boolean,
) {
    if (isLoading && posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }
    if (posts.isEmpty()) {
        EmptyFeedState()
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = posts, key = Post::id) { post ->
            FeedPostCard(
                post = post,
                currentUserId = currentUserId,
                selectedForComments = post.id == selectedPostId,
                onLikeToggleClick = onLikeToggleClick,
                onCommentClick = onCommentClick,
            )
        }
    }
}

@Composable
private fun NewPostRoute(
    userName: String,
    newPostText: String,
    isLoading: Boolean,
    onTextChange: (String) -> Unit,
    onCreatePostClick: () -> Unit,
) {
    NewPostComposer(
        userName = userName,
        text = newPostText,
        isLoading = isLoading,
        onTextChange = onTextChange,
        onPublishClick = onCreatePostClick,
    )
}
