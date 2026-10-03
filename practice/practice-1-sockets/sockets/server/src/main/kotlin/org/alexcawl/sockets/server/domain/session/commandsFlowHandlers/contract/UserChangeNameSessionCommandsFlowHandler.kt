package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.UserChangeNameResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.UserChangeNameEvent
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UserChangeNameCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.cancellation.CancellationException

internal class UserChangeNameSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UserChangeNameCommand>().map { command: UserChangeNameCommand ->
            try {
                newSuspendedTransaction(context = Dispatchers.IO, db = database) {
                    execute(command = command)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Throwable) {
                exception.asResponse() to null
            }
        }.onEach { (serverResponse: ServerResponse, oldUser: User?) ->
            if (serverResponse is UserChangeNameResponse.Success) {
                val timestamp: Long = generateTimestamp()
                dispatch(timestamp = timestamp, user = serverResponse.user, oldUser = oldUser)
            }
        }.map { (serverResponse: ServerResponse, _: User?) ->
            OnReceiveServerResponseEvent(response = serverResponse)
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: UserChangeNameCommand): Pair<UserChangeNameResponse, User?> {
        val userEntity: UserEntity = UserEntity.findById(id = command.userId)
            ?: return UserChangeNameResponse.UserNotFound to null
        val oldUser = userEntity.asDomain()
        userEntity.name = command.userName
        val user: User = userEntity.asDomain()
        return UserChangeNameResponse.Success(user = user) to oldUser
    }

    private fun dispatch(timestamp: Long, user: User, oldUser: User?) {
        globalStore.dispatch(event = UserChangeNameEvent(user = user, oldUser = oldUser, timestamp = timestamp))
    }
}
