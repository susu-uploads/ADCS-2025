package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.entity.User
import org.alexcawl.sockets.server.data.entity.UserConnectionTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.entity.UserTypeEntity
import org.alexcawl.sockets.server.data.table.Users
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.CreateSystemUserCommand
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnCreatedSystemUserEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal class CreateSystemUserGlobalCommandsFlowHandler(
    private val systemUserName: String,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<CreateSystemUserCommand>().map(transform = ::execute)
    }

    private suspend fun execute(command: CreateSystemUserCommand): GlobalEvent {
        return newSuspendedTransaction(context = Dispatchers.IO, db = database) {
            val userEntity: UserEntity = UserEntity
                .find { (Users.type eq UserTypeEntity.SYSTEM) and (Users.name eq systemUserName) }
                .singleOrNull()
                ?: UserEntity.new {
                    this.name = systemUserName
                    this.type = UserTypeEntity.SYSTEM
                    this.connectionType = UserConnectionTypeEntity.ONLINE
                }
            userEntity.asDomain()
        }.let { user: User ->
            OnCreatedSystemUserEvent(systemUser = user)
        }
    }
}
