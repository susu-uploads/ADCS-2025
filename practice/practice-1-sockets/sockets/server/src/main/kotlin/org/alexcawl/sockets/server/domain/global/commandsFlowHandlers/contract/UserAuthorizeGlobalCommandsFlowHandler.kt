package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMembersEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateChatMessagesEvent
import org.alexcawl.sockets.server.domain.global.OnUpdateUsersEvent
import org.alexcawl.sockets.server.domain.global.UserAuthorizeCommand
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

internal class UserAuthorizeGlobalCommandsFlowHandler(
    private val timestampFormatter: TimestampFormatter,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<UserAuthorizeCommand>().transform { command: UserAuthorizeCommand ->
            if (command.systemUserId != null && command.systemChatId != null) {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(systemUserId = command.systemUserId, systemChatId = command.systemChatId, command = command)
                }
                emit(value = OnUpdateUsersEvent)
                emit(value = OnUpdateChatMembersEvent(chatId = command.systemChatId))
                emit(value = OnUpdateChatMessagesEvent(chatId = command.systemChatId))
            }
        }
    }

    private suspend fun Transaction.execute(systemUserId: UUID, systemChatId: Int, command: UserAuthorizeCommand) {
        val isUserJoinedSystemChat: Boolean = ChatMembers.selectAll()
            .where {
                (ChatMembers.memberId eq command.user.id) and
                (ChatMembers.chatId eq systemChatId)
            }
            .limit(n = 1)
            .any()
        if (!isUserJoinedSystemChat) {
            ChatMembers.insert { builder: UpdateBuilder<*> ->
                builder[ChatMembers.chatId] = systemChatId
                builder[ChatMembers.memberId] = command.user.id
            }
        }
        val systemMessage: String = buildSystemMessage(command = command)
        sendMessage(authorId = systemUserId, chatId = systemChatId, message = systemMessage)
    }

    private fun buildSystemMessage(command: UserAuthorizeCommand): String {
        val formattedTimestamp: String = timestampFormatter.format(timestamp = command.timestamp)
        return "[$formattedTimestamp] User \"${command.user.name}\" joined server"
    }
}
