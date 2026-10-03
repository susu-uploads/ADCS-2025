package org.alexcawl.sockets.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.alexcawl.sockets.contract.entity.UserConnectionType
import org.alexcawl.sockets.contract.entity.UserType
import org.alexcawl.sockets.contract.request.ChatCreateRequest
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import org.alexcawl.sockets.contract.request.UserDeauthorizeRequest
import org.alexcawl.sockets.contract.response.NotAuthorizedResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.contract.response.UserDeauthorizeResponse
import org.alexcawl.sockets.server.testkit.application
import org.alexcawl.sockets.server.testkit.receiveResponse
import org.alexcawl.sockets.server.testkit.applicationConfiguration
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ServerAuthorizationContractTest {

    @Test
    fun `Unauthorized client gets not authorized response for protected request`(): Unit = runTest {
        val configuration: ApplicationConfiguration = applicationConfiguration()
        application(configuration = configuration) {
            client {
                sendRequest(request = ChatCreateRequest(chatName = "forbidden-chat"))
                receiveResponse<NotAuthorizedResponse>()
            }
        }
    }

    @Test
    fun `Unauthorized client gets not authorized response for deauthorize request`(): Unit = runTest {
        val configuration: ApplicationConfiguration = applicationConfiguration()
        application(configuration = configuration) {
            client {
                sendRequest(request = UserDeauthorizeRequest)
                receiveResponse<NotAuthorizedResponse>()
            }
        }
    }

    @Test
    fun `Client can authorize and deauthorize`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val userName: String = "user-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                    response.user.id == userId
                }.apply {
                    assertEquals(expected = userId, actual = user.id)
                    assertEquals(expected = userName, actual = user.name)
                    assertEquals(expected = UserType.DEFAULT, actual = user.type)
                    assertEquals(expected = UserConnectionType.ONLINE, actual = user.connectionType)
                }

                sendRequest(request = UserDeauthorizeRequest)
                receiveResponse<UserDeauthorizeResponse.Success> { response: UserDeauthorizeResponse.Success ->
                    response.user.id == userId
                }.apply {
                    assertEquals(expected = userId, actual = user.id)
                    assertEquals(expected = userName, actual = user.name)
                    assertEquals(expected = UserType.DEFAULT, actual = user.type)
                    assertEquals(expected = UserConnectionType.OFFLINE, actual = user.connectionType)
                }
            }
        }
    }

    @Test
    fun `Authorize without userName generates default name`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = null))
                receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                    response.user.id == userId
                }.apply {
                    assertEquals(expected = userId, actual = user.id)
                    assertTrue(actual = user.name.startsWith(prefix = "aboba-"))
                }
            }
        }
    }

    @Test
    fun `Second authorize in the same session returns already authorized`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val userName: String = "user-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                    response.user.id == userId
                }

                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.AlreadyAuthorized>()
            }
        }
    }

    @Test
    fun `Second session cannot authorize same user while first session is online`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val userName: String = "user-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val firstAuthorized: CompletableDeferred<Unit> = CompletableDeferred()
        val releaseFirstClient: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val firstClientJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                    receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                        response.user.id == userId
                    }
                    firstAuthorized.complete(value = Unit)
                    releaseFirstClient.await()
                }
            }

            val secondClientJob = launch {
                firstAuthorized.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                    receiveResponse<UserAuthorizeResponse.AlreadyAuthorized>()
                }
                releaseFirstClient.complete(value = Unit)
            }

            joinAll(firstClientJob, secondClientJob)
        }
    }

    @Test
    fun `Deauthorized user can authorize again from a new connection`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val userName: String = "user-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                    response.user.id == userId
                }

                sendRequest(request = UserDeauthorizeRequest)
                receiveResponse<UserDeauthorizeResponse.Success> { response: UserDeauthorizeResponse.Success ->
                    response.user.id == userId
                }
            }

            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = userName))
                receiveResponse<UserAuthorizeResponse.Success> { response: UserAuthorizeResponse.Success ->
                    response.user.id == userId && response.user.name == userName
                }
            }
        }
    }
}
