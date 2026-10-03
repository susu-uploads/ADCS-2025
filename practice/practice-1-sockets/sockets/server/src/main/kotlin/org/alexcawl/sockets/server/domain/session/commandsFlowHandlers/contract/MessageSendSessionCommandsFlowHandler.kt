package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.entity.Message
import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.MessageSendResponse
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.ChatTypeEntity
import org.alexcawl.sockets.server.data.entity.MessageEntity
import org.alexcawl.sockets.server.data.entity.MessageTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.MessageSendEvent
import org.alexcawl.sockets.server.domain.session.MessageSendCommand
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class MessageSendSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<MessageSendCommand>().map { command: MessageSendCommand ->
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
            if (serverResponse is MessageSendResponse.Success) {
                dispatch(chat = serverResponse.chat)
            }
        }.map { serverResponse: ServerResponse ->
            OnReceiveServerResponseEvent(response = serverResponse)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: MessageSendCommand): MessageSendResponse {
        val timestamp: Long = generateTimestamp()
        val chatEntity: ChatEntity = ChatEntity.findById(id = command.chatId)
            ?: return MessageSendResponse.ChatNotFound
        if (chatEntity.type == ChatTypeEntity.READ_ONLY) {
            return MessageSendResponse.ChatIsReadOnly
        }
        val userEntity: UserEntity = UserEntity.findById(id = command.userId)
            ?: return MessageSendResponse.UserNotFound
        val isUserJoinedChat: Boolean = ChatMembers.selectAll()
            .where {
                (ChatMembers.memberId eq command.userId) and
                (ChatMembers.chatId eq command.chatId)
            }
            .limit(n = 1)
            .any()
        if (!isUserJoinedChat) {
            return MessageSendResponse.UserIsNotInChat
        }
        val messageEntity: MessageEntity = MessageEntity.new {
            this.author = userEntity
            this.chat = chatEntity
            this.timestamp = timestamp
            this.text = command.message
            this.type = when (command.type) {
                MessageType.TEXT -> MessageTypeEntity.TEXT
            }
        }
        val chat: Chat = chatEntity.asDomain()
        val user: User = userEntity.asDomain()
        val message: Message = messageEntity.asDomain()
        return MessageSendResponse.Success(chat = chat, user = user, message = message)
    }

    private fun dispatch(chat: Chat) {
        globalStore.dispatch(event = MessageSendEvent(chat = chat))
    }
}
