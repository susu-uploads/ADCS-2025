package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.ChatLeaveResponse
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.ChatLeaveEvent
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.session.ChatLeaveCommand
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class ChatLeaveSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<ChatLeaveCommand>().map { command: ChatLeaveCommand ->
            try {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(command = command)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Throwable) {
                exception.asResponse()
            }
        }.onEach { serverResponse: ServerResponse ->
            if (serverResponse is ChatLeaveResponse.Success) {
                val timestamp: Long = generateTimestamp()
                dispatch(chat = serverResponse.chat, user = serverResponse.user, timestamp = timestamp)
            }
        }.map { serverResponse: ServerResponse ->
            OnReceiveServerResponseEvent(response = serverResponse)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: ChatLeaveCommand): ChatLeaveResponse {
        val chatEntity: ChatEntity = ChatEntity.findById(id = command.chatId)
            ?: return ChatLeaveResponse.ChatNotFound
        val userEntity: UserEntity = UserEntity.findById(id = command.userId)
            ?: return ChatLeaveResponse.UserNotFound
        val deletedRows: Int = ChatMembers.deleteWhere {
            (ChatMembers.chatId eq chatEntity.id) and
            (ChatMembers.memberId eq userEntity.id)
        }
        if (deletedRows == 0) return ChatLeaveResponse.AlreadyNotInChat
        val chat: Chat = chatEntity.asDomain()
        val user: User = userEntity.asDomain()
        return ChatLeaveResponse.Success(chat = chat, user = user)
    }

    private fun dispatch(chat: Chat, user: User, timestamp: Long) {
        globalStore.dispatch(event = ChatLeaveEvent(user = user, chat = chat, timestamp = timestamp))
    }
}
