package org.alexcawl.sockets.server.domain.session.commandsFlowHandlers.contract

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.common.generateTimestamp
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.contract.response.ServerResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.contract.response.asResponse
import org.alexcawl.sockets.server.data.entity.UserConnectionTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.entity.UserTypeEntity
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.GlobalStore
import org.alexcawl.sockets.server.domain.global.UserAuthorizeEvent
import org.alexcawl.sockets.server.domain.session.OnAuthorizeSessionEvent
import org.alexcawl.sockets.server.domain.session.OnReceiveServerResponseEvent
import org.alexcawl.sockets.server.domain.session.OnUpdateChatsEvent
import org.alexcawl.sockets.server.domain.session.SessionCommand
import org.alexcawl.sockets.server.domain.session.SessionEvent
import org.alexcawl.sockets.server.domain.session.UserAuthorizeCommand
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

internal class UserAuthorizeSessionCommandsFlowHandler(
    private val globalStore: GlobalStore,
    private val database: Database,
) : CommandsFlowHandler<SessionCommand, SessionEvent> {

    override fun handle(commands: Flow<SessionCommand>): Flow<SessionEvent> {
        return commands.filterIsInstance<UserAuthorizeCommand>().map { command: UserAuthorizeCommand ->
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
            if (serverResponse is UserAuthorizeResponse.Success) {
                val timestamp: Long = generateTimestamp()
                dispatch(user = serverResponse.user, timestamp = timestamp)
            }
        }.transform { serverResponse: ServerResponse ->
            if (serverResponse is UserAuthorizeResponse.Success) {
                emit(value = OnAuthorizeSessionEvent(userId = serverResponse.user.id))
            }
            emit(value = OnReceiveServerResponseEvent(response = serverResponse))
            if (serverResponse is UserAuthorizeResponse.Success) {
                emit(value = OnUpdateChatsEvent)
            }
        }
    }

    @Suppress("RedundantSuspendModifier", "UnusedReceiverParameter")
    private suspend fun Transaction.execute(command: UserAuthorizeCommand): ServerResponse {
        val userEntity: UserEntity? = UserEntity.findById(id = command.userId)
        return when {
            userEntity == null -> {
                val newUserEntity: UserEntity = UserEntity.new(id = command.userId) {
                    name = command.userName ?: generateName(userId = command.userId)
                    type = UserTypeEntity.DEFAULT
                    connectionType = UserConnectionTypeEntity.ONLINE
                }
                val user: User = newUserEntity.asDomain()
                UserAuthorizeResponse.Success(user = user)
            }

            userEntity.connectionType == UserConnectionTypeEntity.ONLINE -> UserAuthorizeResponse.AlreadyAuthorized

            else -> {
                userEntity.apply {
                    if (command.userName != null) {
                        this.name = command.userName
                    }
                    this.connectionType = UserConnectionTypeEntity.ONLINE
                }
                val user: User = userEntity.asDomain()
                UserAuthorizeResponse.Success(user = user)
            }
        }
    }

    private fun dispatch(user: User, timestamp: Long) {
        globalStore.dispatch(event = UserAuthorizeEvent(user = user, timestamp = timestamp))
    }

    private fun generateName(userId: UUID): String {
        return "aboba-${userId.hashCode()}"
    }
}
