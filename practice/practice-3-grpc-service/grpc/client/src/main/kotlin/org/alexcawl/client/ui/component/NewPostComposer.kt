package org.alexcawl.client.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.grpc.client.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NewPostComposer(
    userName: String,
    text: String,
    isLoading: Boolean,
    onTextChange: (String) -> Unit,
    onPublishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title: String = stringResource(Res.string.new_post_title)
    val authorText: String = stringResource(Res.string.author_format, userName)
    val inputLabel: String = stringResource(Res.string.new_post_input_label)
    val publishText: String = stringResource(Res.string.action_publish_post)
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = authorText, style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onTextChange,
                minLines = 6,
                label = { Text(inputLabel) },
            )
            Button(
                onClick = onPublishClick,
                enabled = !isLoading,
            ) {
                Text(publishText)
            }
        }
    }
}
