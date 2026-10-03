package org.alexcawl.client.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.grpc.client.generated.resources.*
import org.alexcawl.contract.entity.Post
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CommentPanel(
    post: Post,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val commentsTitle: String = stringResource(Res.string.comments_title)
    val commentsEmptyText: String = stringResource(Res.string.comments_empty)
    val commentInputLabel: String = stringResource(Res.string.comments_input_label)
    val sendText: String = stringResource(Res.string.action_send)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = commentsTitle, style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = post.comments, key = { comment -> comment.id }) { comment ->
                CommentCard(comment = comment)
            }
        }
        if (post.comments.isEmpty()) {
            Text(
                text = commentsEmptyText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            value = draft,
            onValueChange = onDraftChange,
            label = { Text(commentInputLabel) },
        )
        Button(onClick = onSendClick) {
            Text(sendText)
        }
    }
}
