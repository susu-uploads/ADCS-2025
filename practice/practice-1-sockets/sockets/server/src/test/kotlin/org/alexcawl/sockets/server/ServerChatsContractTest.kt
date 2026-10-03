package org.alexcawl.sockets.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.request.ChatCreateRequest
import org.alexcawl.sockets.contract.request.ChatJoinRequest
import org.alexcawl.sockets.contract.request.ChatLeaveRequest
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import org.alexcawl.sockets.contract.response.ChatCreateResponse
import org.alexcawl.sockets.contract.response.ChatJoinResponse
import org.alexcawl.sockets.contract.response.ChatLeaveResponse
import org.alexcawl.sockets.contract.response.UpdateChatMembersResponse
import org.alexcawl.sockets.contract.response.UpdateChatsResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.server.testkit.application
import org.alexcawl.sockets.server.testkit.receiveResponse
import org.alexcawl.sockets.server.testkit.applicationConfiguration
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ServerChatsContractTest {

    @Test
    fun `Authorized client can create chat join and leave it`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val userName: String = "chat-user-$userId"
        val chatName: String = "chat-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatCreateRequest(chatName = chatName))
                val createdChat = receiveResponse<ChatCreateResponse.Success>().chat
                assertEquals(expected = chatName, actual = createdChat.name)
                assertEquals(expected = ChatType.DEFAULT, actual = createdChat.type)

                sendRequest(request = ChatJoinRequest(chatId = createdChat.id))
                receiveResponse<ChatJoinResponse.Success>().apply {
                    assertEquals(expected = createdChat.id, actual = chat.id)
                    assertEquals(expected = userId, actual = user.id)
                }

                sendRequest(request = ChatLeaveRequest(chatId = createdChat.id))
                receiveResponse<ChatLeaveResponse.Success>().apply {
                    assertEquals(expected = createdChat.id, actual = chat.id)
                    assertEquals(expected = userId, actual = user.id)
                }
            }
        }
    }

    @Test
    fun `Join and leave unknown chat return chat not found`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val unknownChatId: Int = Int.MAX_VALUE
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatJoinRequest(chatId = unknownChatId))
                receiveResponse<ChatJoinResponse.ChatNotFound>()

                sendRequest(request = ChatLeaveRequest(chatId = unknownChatId))
                receiveResponse<ChatLeaveResponse.ChatNotFound>()
            }
        }
    }

    @Test
    fun `Duplicate join returns already in chat`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatCreateRequest(chatName = "chat-$userId"))
                val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                sendRequest(request = ChatJoinRequest(chatId = chatId))
                receiveResponse<ChatJoinResponse.Success> { response -> response.chat.id == chatId && response.user.id == userId }

                sendRequest(request = ChatJoinRequest(chatId = chatId))
                receiveResponse<ChatJoinResponse.AlreadyInChat>()
            }
        }
    }

    @Test
    fun `Leaving chat without membership returns already not in chat`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatCreateRequest(chatName = "chat-$userId"))
                val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                sendRequest(request = ChatLeaveRequest(chatId = chatId))
                receiveResponse<ChatLeaveResponse.AlreadyNotInChat>()
            }
        }
    }

    @Test
    fun `Authorized user is added to system chat by default`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val systemChatName: String = "system-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration().copy(
            systemChatName = systemChatName,
        )

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "member-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                val systemChatId: Int = receiveResponse<UpdateChatsResponse> { response: UpdateChatsResponse ->
                    response.chats.any { chat -> chat.name == systemChatName && chat.type == ChatType.READ_ONLY }
                }.findSystemChat(systemChatName = systemChatName).id

                receiveResponse<UpdateChatMembersResponse> { response: UpdateChatMembersResponse ->
                    response.chatId == systemChatId && userId in response.memberIds
                }

                sendRequest(request = ChatLeaveRequest(chatId = systemChatId))
                receiveResponse<ChatLeaveResponse.Success> { response: ChatLeaveResponse.Success ->
                    response.chat.id == systemChatId && response.user.id == userId
                }
            }
        }
    }

    @Test
    fun `Created chats are visible to other authorized users via updates`(): Unit = runTest {
        val observerId: UUID = UUID.randomUUID()
        val creatorId: UUID = UUID.randomUUID()
        val chatName: String = "shared-$creatorId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val observerReady: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val observerJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = observerId, userName = "observer-$observerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == observerId }
                    observerReady.complete(value = Unit)

                    receiveResponse<UpdateChatsResponse> { response: UpdateChatsResponse ->
                        response.chats.any { chat -> chat.name == chatName }
                    }.apply {
                        assertTrue(actual = chats.any { chat -> chat.name == chatName })
                    }
                }
            }

            val creatorJob = launch {
                observerReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = creatorId, userName = "creator-$creatorId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == creatorId }

                    sendRequest(request = ChatCreateRequest(chatName = chatName))
                    receiveResponse<ChatCreateResponse.Success> { response: ChatCreateResponse.Success ->
                        response.chat.name == chatName
                    }
                }
            }

            joinAll(observerJob, creatorJob)
        }
    }
}
