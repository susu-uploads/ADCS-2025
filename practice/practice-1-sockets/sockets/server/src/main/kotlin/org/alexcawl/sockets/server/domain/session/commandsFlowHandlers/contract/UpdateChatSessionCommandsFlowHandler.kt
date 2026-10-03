package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.entity.Message
import org.alexcawl.sockets.contract.response.UpdateChatMessagesResponse
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.MessageEntity
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.data.table.Messages
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UpdateChatMessagesCommand
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class UpdateChatSessionCommandsFlowHandler(
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UpdateChatMessagesCommand>().mapNotNull { command: UpdateChatMessagesCommand ->
            try {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(command = command)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Throwable) {
                exception.asResponse()
            }
        }.map { serverResponse: ServerResponse ->
            OnReceiveServerResponseEvent(response = serverResponse)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: UpdateChatMessagesCommand): UpdateChatMessagesResponse? {
        val isUserJoinedChat: Boolean = ChatMembers.selectAll()
            .where {
                (ChatMembers.memberId eq command.userId) and
                (ChatMembers.chatId eq command.chatId)
            }
            .limit(n = 1)
            .any()
        if (!isUserJoinedChat) return null
        return ChatEntity.findById(id = command.chatId)?.let { chatEntity: ChatEntity ->
            val messages: List<Message> = chatEntity.messages
                .orderBy(Messages.timestamp to SortOrder.ASC)
                .map(transform = MessageEntity::asDomain)
            UpdateChatMessagesResponse(
                chatId = chatEntity.id.value,
                messages = messages,
            )
        }
    }
}
