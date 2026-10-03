package org.alexcawl.sockets.server.domain.global.commandsFlowHandlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.alexcawl.kotea.CommandsFlowHandler
import org.alexcawl.sockets.contract.entity.Chat
import org.alexcawl.sockets.server.data.entity.ChatEntity
import org.alexcawl.sockets.server.data.entity.ChatTypeEntity
import org.alexcawl.sockets.server.data.entity.UserEntity
import org.alexcawl.sockets.server.data.table.ChatMembers
import org.alexcawl.sockets.server.data.table.Chats
import org.alexcawl.sockets.server.domain.asDomain
import org.alexcawl.sockets.server.domain.global.CreateSystemChatCommand
import org.alexcawl.sockets.server.domain.global.GlobalCommand
import org.alexcawl.sockets.server.domain.global.GlobalEvent
import org.alexcawl.sockets.server.domain.global.OnCreatedSystemChatEvent
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

internal class CreateSystemChatGlobalCommandsFlowHandler(
    private val systemChatName: String,
    private val database: Database,
) : CommandsFlowHandler<GlobalCommand, GlobalEvent> {

    override fun handle(commands: Flow<GlobalCommand>): Flow<GlobalEvent> {
        return commands.filterIsInstance<CreateSystemChatCommand>().map(transform = ::execute)
    }

    private suspend fun execute(command: CreateSystemChatCommand): GlobalEvent {
        return newSuspendedTransaction(context = Dispatchers.IO, db = database) {
            val chatEntity: ChatEntity = ChatEntity
                .find { (Chats.type eq ChatTypeEntity.READ_ONLY) and (Chats.name eq systemChatName) }
                .singleOrNull()
                ?: ChatEntity.new {
                    this.name = systemChatName
                    this.type = ChatTypeEntity.READ_ONLY
                }
            val systemChatMembersIds: Set<UUID> = ChatMembers.selectAll()
                .where { ChatMembers.chatId eq chatEntity.id }
                .map { row: ResultRow -> row[ChatMembers.memberId] }
                .map { entityID: EntityID<UUID> -> entityID.value }
                .toSet()
            UserEntity.all().forEach { userEntity: UserEntity ->
                if (userEntity.id.value !in systemChatMembersIds) {
                    ChatMembers.insert { builder: UpdateBuilder<*> ->
                        builder[ChatMembers.chatId] = chatEntity.id
                        builder[ChatMembers.memberId] = userEntity.id
                    }
                }
            }
            chatEntity.asDomain()
        }.let { chat: Chat ->
            OnCreatedSystemChatEvent(systemChat = chat)
        }
    }
}
