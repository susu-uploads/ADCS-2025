package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMessagesEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateUsersEvent
import org.alexcawl.sockets.server.domain.global.UserChangeNameCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class UserChangeNameGlobalCommandsFlowHandler(
    private val timestampFormatter: TimestampFormatter,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<UserChangeNameCommand>().transform { command: UserChangeNameCommand ->
            if (command.systemUserId != null && command.systemChatId != null) {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(systemUserId = command.systemUserId, systemChatId = command.systemChatId, command = command)
                }
                emit(value = OnUpdateChatMessagesEvent(chatId = command.systemChatId))
            }
            emit(value = OnUpdateUsersEvent)
        }
    }

    private suspend fun Transaction.execute(systemUserId: UUID, systemChatId: Int, command: UserChangeNameCommand) {
        val systemMessage: String = buildSystemMessage(command = command)
        sendMessage(authorId = systemUserId, chatId = systemChatId, message = systemMessage)
    }

    private fun buildSystemMessage(command: UserChangeNameCommand): String {
        val formattedTimestamp: String = timestampFormatter.format(timestamp = command.timestamp)
        return "[$formattedTimestamp] User \"${command.oldUserName}\" is now \"${command.newUserName}\""
    }
}
