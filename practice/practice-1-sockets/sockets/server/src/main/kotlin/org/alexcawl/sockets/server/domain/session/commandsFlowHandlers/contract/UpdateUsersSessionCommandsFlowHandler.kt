package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.UpdateUsersResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UpdateUsersCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class UpdateUsersSessionCommandsFlowHandler(
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UpdateUsersCommand>().map { command: UpdateUsersCommand ->
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
    private suspend fun Transaction.execute(command: UpdateUsersCommand): UpdateUsersResponse {
        val userEntities: SizedIterable<UserEntity> = UserEntity.all()
        return UpdateUsersResponse(users = userEntities.map(transform = UserEntity::asDomain))
    }
}
