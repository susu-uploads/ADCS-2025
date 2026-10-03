package org.alexcawl.client.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.contract.entity.Post
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedPostCard(
    post: Post,
    currentUserId: String,
    selectedForComments: Boolean,
    onLikeToggleClick: (Long, Boolean) -> Unit,
    onCommentClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLikedByCurrentUser: Boolean = post.likedBy.any { user -> user.id == currentUserId }
    val likeText: String = if (isLikedByCurrentUser) {
        stringResource(Res.string.post_liked_count, post.likedBy.size)
    } else {
        stringResource(Res.string.post_like_count, post.likedBy.size)
    }
    val commentText: String = stringResource(Res.string.post_comments_count, post.comments.size)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = post.author.name, style = MaterialTheme.typography.titleMedium)
            Text(text = post.text, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isLikedByCurrentUser) {
                    Button(onClick = { onLikeToggleClick(post.id, true) }) {
                        Text(likeText)
                    }
                } else {
                    OutlinedButton(onClick = { onLikeToggleClick(post.id, false) }) {
                        Text(likeText)
                    }
                }

                if (selectedForComments) {
                    OutlinedButton(onClick = { onCommentClick(post.id) }) {
                        Text(commentText)
                    }
                } else {
                    Button(onClick = { onCommentClick(post.id) }) {
                        Text(commentText)
                    }
                }
            }
        }
    }
}
