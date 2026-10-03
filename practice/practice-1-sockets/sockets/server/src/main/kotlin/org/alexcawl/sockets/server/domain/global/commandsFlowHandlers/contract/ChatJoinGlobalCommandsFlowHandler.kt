package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.ChatJoinCommand
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMembersEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMessagesEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class ChatJoinGlobalCommandsFlowHandler(
    private val timestampFormatter: TimestampFormatter,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<ChatJoinCommand>().transform { command: ChatJoinCommand ->
            if (command.systemUserId != null) {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(systemUserId = command.systemUserId, command = command)
                }
                emit(value = OnUpdateChatMembersEvent(chatId = command.chat.id))
                emit(value = OnUpdateChatMessagesEvent(chatId = command.chat.id))
            }
        }
    }

    private suspend fun Transaction.execute(systemUserId: UUID, command: ChatJoinCommand) {
        val systemMessage: String = buildSystemMessage(command = command)
        sendMessage(authorId = systemUserId, chatId = command.chat.id, message = systemMessage)
    }

    private fun buildSystemMessage(command: ChatJoinCommand): String {
        val formattedTimestamp: String = timestampFormatter.format(timestamp = command.timestamp)
        return "[$formattedTimestamp] User ${command.user.name} joined chat \"${command.chat.name}\""
    }
}
