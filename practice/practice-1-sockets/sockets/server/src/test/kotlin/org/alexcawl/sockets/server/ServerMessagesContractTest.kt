package org.alexcawl.sockets.server

import kotlinx.coroutines.test.runTest
import org.alexcawl.sockets.contract.entity.ChatType
import org.alexcawl.sockets.contract.entity.MessageType
import org.alexcawl.sockets.contract.request.ChatCreateRequest
import org.alexcawl.sockets.contract.request.ChatJoinRequest
import org.alexcawl.sockets.contract.request.MessageSendRequest
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import org.alexcawl.sockets.contract.response.ChatCreateResponse
import org.alexcawl.sockets.contract.response.ChatJoinResponse
import org.alexcawl.sockets.contract.response.MessageSendResponse
import org.alexcawl.sockets.contract.response.UpdateChatsResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.server.testkit.application
import org.alexcawl.sockets.server.testkit.receiveResponse
import org.alexcawl.sockets.server.testkit.applicationConfiguration
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ServerMessagesContractTest {

    @Test
    fun `Joined member can send message to chat`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val chatName: String = "messages-$userId"
        val text: String = "hello-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "author-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatCreateRequest(chatName = chatName))
                val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                sendRequest(request = ChatJoinRequest(chatId = chatId))
                receiveResponse<ChatJoinResponse.Success> { response ->
                    response.chat.id == chatId && response.user.id == userId
                }

                sendRequest(request = MessageSendRequest(chatId = chatId, message = text, messageType = MessageType.TEXT))
                receiveResponse<MessageSendResponse.Success>().apply {
                    assertEquals(expected = chatId, actual = chat.id)
                    assertEquals(expected = userId, actual = user.id)
                    assertEquals(expected = text, actual = message.text)
                    assertEquals(expected = MessageType.TEXT, actual = message.type)
                }
            }
        }
    }

    @Test
    fun `Message send to unknown chat returns chat not found`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = MessageSendRequest(chatId = Int.MAX_VALUE, message = "unknown-chat", messageType = MessageType.TEXT))
                receiveResponse<MessageSendResponse.ChatNotFound>()
            }
        }
    }

    @Test
    fun `Message send without membership returns user is not in chat`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "user-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = ChatCreateRequest(chatName = "detached-$userId"))
                val chatId: Int = receiveResponse<ChatCreateResponse.Success>().chat.id

                sendRequest(request = MessageSendRequest(chatId = chatId, message = "not-member", messageType = MessageType.TEXT))
                receiveResponse<MessageSendResponse.UserIsNotInChat>()
            }
        }
    }

    @Test
    fun `Message send to read only system chat returns chat is read only`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val systemChatName: String = "system-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration().copy(
            systemChatName = systemChatName,
        )

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = "reader-$userId"))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                val systemChatId: Int = receiveResponse<UpdateChatsResponse> { response: UpdateChatsResponse ->
                    response.chats.any { chat -> chat.name == systemChatName && chat.type == ChatType.READ_ONLY }
                }.findSystemChat(systemChatName = systemChatName).id

                sendRequest(
                    request = MessageSendRequest(
                        chatId = systemChatId,
                        message = "forbidden",
                        messageType = MessageType.TEXT,
                    )
                )
                receiveResponse<MessageSendResponse.ChatIsReadOnly>()
            }
        }
    }
}
