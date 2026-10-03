package org.alexcawl.sockets.client.ui.main.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alexcawl.sockets.client.ui.main.MainChatItemUiState

@Composable
internal fun MainChatsPanel(
    chats: List<MainChatItemUiState>,
    onChatClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "My chats",
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = chats, key = { chat: MainChatItemUiState -> chat.id }) { chat: MainChatItemUiState ->
                MainChatItem(
                    chat = chat,
                    onClick = {
                        onChatClick(chat.id)
                    }
                )
            }
        }
    }
}
