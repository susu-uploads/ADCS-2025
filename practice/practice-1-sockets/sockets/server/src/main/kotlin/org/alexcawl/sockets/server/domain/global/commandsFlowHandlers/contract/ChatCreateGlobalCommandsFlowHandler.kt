package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.ChatCreateCommand
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMessagesEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatsEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class ChatCreateGlobalCommandsFlowHandler(
    private val timestampFormatter: TimestampFormatter,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<ChatCreateCommand>().transform { command: ChatCreateCommand ->
            if (command.systemUserId != null) {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(systemUserId = command.systemUserId, command = command)
                }
            }
            emit(value = OnUpdateChatsEvent)
            emit(value = OnUpdateChatMessagesEvent(chatId = command.chat.id))
        }
    }

    private suspend fun Transaction.execute(systemUserId: UUID, command: ChatCreateCommand) {
        val systemMessage: String = buildSystemMessage(command = command)
        sendMessage(authorId = systemUserId, chatId = command.chat.id, message = systemMessage)
    }

    private fun buildSystemMessage(command: ChatCreateCommand): String {
        val formattedTimestamp: String = timestampFormatter.format(timestamp = command.timestamp)
        return "[$formattedTimestamp] Chat \"${command.chat.name}\" created"
    }
}
