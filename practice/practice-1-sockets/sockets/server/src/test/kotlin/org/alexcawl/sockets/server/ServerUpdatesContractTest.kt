package org.alexcawl.sockets.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.request.ChatCreateRequest
import org.alexcawl.sockets.contract.request.ChatJoinRequest
import org.alexcawl.sockets.contract.request.ChatLeaveRequest
import org.alexcawl.sockets.contract.request.MessageSendRequest
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import org.alexcawl.sockets.contract.response.ChatCreateResponse
import org.alexcawl.sockets.contract.response.ChatJoinResponse
import org.alexcawl.sockets.contract.response.ChatLeaveResponse
import org.alexcawl.sockets.contract.response.MessageSendResponse
import org.alexcawl.sockets.contract.response.UpdateChatMembersResponse
import org.alexcawl.sockets.contract.response.UpdateChatMessagesResponse
import org.alexcawl.sockets.contract.response.UpdateChatsResponse
import org.alexcawl.sockets.contract.response.UpdateUsersResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.server.testkit.application
import org.alexcawl.sockets.server.testkit.receiveResponse
import org.alexcawl.sockets.server.testkit.applicationConfiguration
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class ServerUpdatesContractTest {

    @Test
    fun `Authorize sends chats users and system chat members updates`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val systemChatName: String = "system-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration().copy(
            systemChatName = systemChatName,
        )

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                val systemChatId: Int = receiveResponse<UpdateChatsResponse> { response: UpdateChatsResponse ->
                    response.chats.any { chat -> chat.name == systemChatName }
                }.findSystemChat(systemChatName = systemChatName).id

                receiveResponse<UpdateUsersResponse> { response: UpdateUsersResponse ->
                    response.users.any { user -> user.id == userId }
                }

                receiveResponse<UpdateChatMembersResponse> { response: UpdateChatMembersResponse ->
                    response.chatId == systemChatId && userId in response.memberIds
                }
            }
        }
    }

    @Test
    fun `Chat member updates are broadcasted when member joins and leaves`(): Unit = runTest {
        val firstUserId: UUID = UUID.randomUUID()
        val secondUserId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val chatIdReady: CompletableDeferred<Int> = CompletableDeferred()

        application(configuration = configuration) {
            val firstClientJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = firstUserId, userName = "first-$firstUserId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == firstUserId }

                    sendRequest(request = ChatCreateRequest(chatName = "room-$firstUserId"))
                    val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == firstUserId
                    }
                    chatIdReady.complete(value = chatId)

                    withTimeout(timeMillis = 3_000) {
                        receiveResponse<UpdateChatMembersResponse> { response: UpdateChatMembersResponse ->
                            response.chatId == chatId &&
                            (firstUserId in response.memberIds && secondUserId in response.memberIds)
                        }
                    }

                    withTimeout(timeMillis = 3_000) {
                        receiveResponse<UpdateChatMembersResponse> { response: UpdateChatMembersResponse ->
                            response.chatId == chatId &&
                            (firstUserId in response.memberIds && secondUserId !in response.memberIds)
                        }
                    }
                }
            }

            val secondClientJob = launch {
                val chatId: Int = chatIdReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = secondUserId, userName = "second-$secondUserId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == secondUserId }

                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == secondUserId
                    }

                    sendRequest(request = ChatLeaveRequest(chatId = chatId))
                    receiveResponse<ChatLeaveResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == secondUserId
                    }
                }
            }

            joinAll(firstClientJob, secondClientJob)
        }
    }

    @Test
    fun `Chat messages update includes messages from other participants`(): Unit = runTest {
        val firstUserId: UUID = UUID.randomUUID()
        val secondUserId: UUID = UUID.randomUUID()
        val messageText: String = "payload-$secondUserId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val chatIdReady: CompletableDeferred<Int> = CompletableDeferred()
        val secondUserCanSendMessage: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val firstClientJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = firstUserId, userName = "reader-$firstUserId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == firstUserId }

                    sendRequest(request = ChatCreateRequest(chatName = "messages-$firstUserId"))
                    val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == firstUserId
                    }
                    chatIdReady.complete(value = chatId)

                    secondUserCanSendMessage.await()
                    receiveResponse<UpdateChatMessagesResponse> { response: UpdateChatMessagesResponse ->
                        response.chatId == chatId && response.messages.any { message ->
                            message.authorId == secondUserId && message.text == messageText
                        }
                    }.apply {
                        assertTrue(actual = messages.any { message ->
                            message.authorId == secondUserId && message.text == messageText
                        })
                    }
                }
            }

            val secondClientJob = launch {
                val chatId: Int = chatIdReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = secondUserId, userName = "writer-$secondUserId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == secondUserId }

                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == secondUserId
                    }

                    secondUserCanSendMessage.complete(value = Unit)
                    sendRequest(request = MessageSendRequest(chatId = chatId, message = messageText, messageType = MessageType.TEXT))
                    receiveResponse<MessageSendResponse.Success> { response: MessageSendResponse.Success ->
                        response.chat.id == chatId &&
                            response.user.id == secondUserId &&
                            response.message.text == messageText
                    }.apply {
                        assertEquals(expected = secondUserId, actual = user.id)
                        assertEquals(expected = messageText, actual = message.text)
                    }
                }
            }

            joinAll(firstClientJob, secondClientJob)
        }
    }

    @Test
    fun `Non member does not receive chat messages update`(): Unit = runTest {
        val ownerId: UUID = UUID.randomUUID()
        val observerId: UUID = UUID.randomUUID()
        val messageText: String = "private-$ownerId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val chatIdReady: CompletableDeferred<Int> = CompletableDeferred()
        val observerReady: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val ownerJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = ownerId, userName = "owner-$ownerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == ownerId }

                    sendRequest(request = ChatCreateRequest(chatName = "private-room-$ownerId"))
                    val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == ownerId
                    }
                    chatIdReady.complete(value = chatId)

                    observerReady.await()
                    sendRequest(request = MessageSendRequest(chatId = chatId, message = messageText, messageType = MessageType.TEXT))
                    receiveResponse<MessageSendResponse.Success> { response: MessageSendResponse.Success ->
                        response.chat.id == chatId && response.message.text == messageText
                    }
                }
            }

            val observerJob = launch {
                val chatId: Int = chatIdReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = observerId, userName = "observer-$observerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == observerId }

                    observerReady.complete(value = Unit)
                    assertFailsWith<TimeoutCancellationException> {
                        withTimeout(timeMillis = 250) {
                            receiveResponse<UpdateChatMessagesResponse> { response: UpdateChatMessagesResponse ->
                                response.chatId == chatId
                            }
                        }
                    }
                }
            }

            joinAll(ownerJob, observerJob)
        }
    }

    @Test
    fun `Non member does not receive chat members update`(): Unit = runTest {
        val ownerId: UUID = UUID.randomUUID()
        val observerId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val chatIdReady: CompletableDeferred<Int> = CompletableDeferred()
        val observerReady: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val ownerJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = ownerId, userName = "owner-$ownerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == ownerId }

                    sendRequest(request = ChatCreateRequest(chatName = "members-room-$ownerId"))
                    val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id
                    chatIdReady.complete(value = chatId)

                    observerReady.await()
                    sendRequest(request = ChatJoinRequest(chatId = chatId))
                    receiveResponse<ChatJoinResponse.Success> { response ->
                        response.chat.id == chatId && response.user.id == ownerId
                    }
                }
            }

            val observerJob = launch {
                val chatId: Int = chatIdReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = observerId, userName = "observer-$observerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == observerId }

                    observerReady.complete(value = Unit)
                    assertFailsWith<TimeoutCancellationException> {
                        withTimeout(timeMillis = 250) {
                            receiveResponse<UpdateChatMembersResponse> { response: UpdateChatMembersResponse ->
                                response.chatId == chatId
                            }
                        }
                    }
                }
            }

            joinAll(ownerJob, observerJob)
        }
    }
}
