package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.NotAuthorizedResponse
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.UserDeauthorizeResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.UserConnectionTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.UserDeauthorizeEvent
import org.alexcawl.sockets.server.domain.session.OnDeauthorizeSessionEvent
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UserDeauthorizeCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class UserDeauthorizeSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UserDeauthorizeCommand>().map { command: UserDeauthorizeCommand ->
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
            if (serverResponse is UserDeauthorizeResponse.Success) {
                val timestamp: Long = generateTimestamp()
                dispatch(timestamp = timestamp, user = serverResponse.user)
            }
        }.transform { serverResponse: ServerResponse ->
            if (serverResponse is UserDeauthorizeResponse.Success) {
                emit(value = OnReceiveServerResponseEvent(response = serverResponse, lastAck = true))
                emit(value = OnDeauthorizeSessionEvent)
            } else {
                emit(value = OnReceiveServerResponseEvent(response = serverResponse))
            }
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: UserDeauthorizeCommand): ServerResponse {
        val userEntity: UserEntity? = UserEntity.findById(id = command.userId)
        return when {
            userEntity == null -> NotAuthorizedResponse

            userEntity.connectionType == UserConnectionTypeEntity.OFFLINE -> NotAuthorizedResponse

            else -> {
                userEntity.apply {
                    this.connectionType = UserConnectionTypeEntity.OFFLINE
                }
                val user: User = userEntity.asDomain()
                UserDeauthorizeResponse.Success(user = user)
            }
        }
    }

    private fun dispatch(timestamp: Long, user: User) {
        globalStore.dispatch(event = UserDeauthorizeEvent(user = user, timestamp = timestamp))
    }
}
