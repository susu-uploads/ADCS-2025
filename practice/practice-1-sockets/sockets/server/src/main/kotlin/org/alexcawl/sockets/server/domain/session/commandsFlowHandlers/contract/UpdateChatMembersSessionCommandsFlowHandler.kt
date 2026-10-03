package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.UpdateChatMembersResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UpdateChatMembersCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class UpdateChatMembersSessionCommandsFlowHandler(
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UpdateChatMembersCommand>().mapNotNull { command: UpdateChatMembersCommand ->
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
    private suspend fun Transaction.execute(command: UpdateChatMembersCommand): UpdateChatMembersResponse? {
        val isUserJoinedChat: Boolean = ChatMembers.selectAll()
            .where {
                (ChatMembers.memberId eq command.userId) and
                    (ChatMembers.chatId eq command.chatId)
            }
            .limit(n = 1)
            .any()
        if (!isUserJoinedChat) return null

        val memberIds = ChatMembers.selectAll()
            .where { ChatMembers.chatId eq command.chatId }
            .map { row -> row[ChatMembers.memberId].value }
            .sortedBy { memberId -> memberId.toString() }

        return UpdateChatMembersResponse(
            chatId = command.chatId,
            memberIds = memberIds,
        )
    }
}
