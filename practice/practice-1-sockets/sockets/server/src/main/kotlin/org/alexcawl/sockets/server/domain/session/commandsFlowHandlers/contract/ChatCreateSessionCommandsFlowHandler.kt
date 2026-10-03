package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.contract.response.ChatCreateResponse
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.ChatTypeEntity
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.ChatCreateEvent
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.session.ChatCreateCommand
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class ChatCreateSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<ChatCreateCommand>().map { command: ChatCreateCommand ->
            try {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(command = command)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                exception.asResponse()
            }
        }.onEach { serverResponse: ServerResponse ->
            if (serverResponse is ChatCreateResponse.Success) {
                val timestamp: Long = generateTimestamp()
                dispatch(chat = serverResponse.chat, timestamp = timestamp)
            }
        }.map { serverResponse: ServerResponse ->
            OnReceiveServerResponseEvent(response = serverResponse)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: ChatCreateCommand): ChatCreateResponse {
        val chatEntity: ChatEntity = ChatEntity.new {
            this.name = command.chatName
            this.type = ChatTypeEntity.DEFAULT
        }
        val chat: Chat = chatEntity.asDomain()
        return ChatCreateResponse.Success(chat = chat)
    }

    private fun dispatch(chat: Chat, timestamp: Long) {
        globalStore.dispatch(event = ChatCreateEvent(chat = chat, timestamp = timestamp))
    }
}
