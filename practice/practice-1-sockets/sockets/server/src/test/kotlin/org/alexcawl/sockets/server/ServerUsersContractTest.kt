package org.alexcawl.sockets.server

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.alexcawl.sockets.contract.entity.UserConnectionType
import org.alexcawl.sockets.contract.request.UserAuthorizeRequest
import org.alexcawl.sockets.contract.request.UserChangeNameRequest
import org.alexcawl.sockets.contract.request.UserDeauthorizeRequest
import org.alexcawl.sockets.contract.response.UpdateUsersResponse
import org.alexcawl.sockets.contract.response.UserAuthorizeResponse
import org.alexcawl.sockets.contract.response.UserChangeNameResponse
import org.alexcawl.sockets.contract.response.UserDeauthorizeResponse
import org.alexcawl.sockets.server.testkit.application
import org.alexcawl.sockets.server.testkit.receiveResponse
import org.alexcawl.sockets.server.testkit.applicationConfiguration
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ServerUsersContractTest {

    @Test
    fun `Authorized client can change own user name`(): Unit = runTest {
        val userId: UUID = UUID.randomUUID()
        val initialUserName: String = "initial-$userId"
        val updatedUserName: String = "updated-$userId"
        val configuration: ApplicationConfiguration = applicationConfiguration()

        application(configuration = configuration) {
            client {
                sendRequest(request = UserAuthorizeRequest(userId = userId, userName = initialUserName))
                receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == userId }

                sendRequest(request = UserChangeNameRequest(userName = updatedUserName))
                receiveResponse<UserChangeNameResponse.Success>().apply {
                    assertEquals(expected = userId, actual = user.id)
                    assertEquals(expected = updatedUserName, actual = user.name)
                }
            }
        }
    }

    @Test
    fun `User list updates are broadcasted on authorize and deauthorize`(): Unit = runTest {
        val firstUserId: UUID = UUID.randomUUID()
        val firstUserName: String = "user-1-$firstUserId"
        val secondUserId: UUID = UUID.randomUUID()
        val secondUserName: String = "user-2-$secondUserId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val firstUserAuthorized: CompletableDeferred<Unit> = CompletableDeferred()
        val secondUserCanDeauthorize: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val firstClientJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = firstUserId, userName = firstUserName))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == firstUserId }
                    firstUserAuthorized.complete(value = Unit)

                    receiveResponse<UpdateUsersResponse> { response: UpdateUsersResponse ->
                        response.users.any { user -> user.id == secondUserId && user.name == secondUserName }
                    }
                    secondUserCanDeauthorize.complete(value = Unit)

                    receiveResponse<UpdateUsersResponse> { response: UpdateUsersResponse ->
                        response.users.any { user -> user.id == firstUserId && user.connectionType == UserConnectionType.ONLINE } &&
                            response.users.any { user -> user.id == secondUserId && user.connectionType == UserConnectionType.OFFLINE }
                    }.apply {
                        assertTrue(actual = users.any { user ->
                            user.id == firstUserId && user.connectionType == UserConnectionType.ONLINE
                        })
                        assertTrue(actual = users.any { user ->
                            user.id == secondUserId && user.connectionType == UserConnectionType.OFFLINE
                        })
                    }
                }
            }

            val secondClientJob = launch {
                firstUserAuthorized.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = secondUserId, userName = secondUserName))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == secondUserId }

                    secondUserCanDeauthorize.await()
                    sendRequest(request = UserDeauthorizeRequest)
                    receiveResponse<UserDeauthorizeResponse.Success> { response: UserDeauthorizeResponse.Success ->
                        response.user.id == secondUserId
                    }
                }
            }

            joinAll(firstClientJob, secondClientJob)
        }
    }

    @Test
    fun `User name change is broadcasted to other users via users update`(): Unit = runTest {
        val observerId: UUID = UUID.randomUUID()
        val actorId: UUID = UUID.randomUUID()
        val actorInitialName: String = "actor-initial-$actorId"
        val actorUpdatedName: String = "actor-updated-$actorId"
        val configuration: ApplicationConfiguration = applicationConfiguration()
        val observerReady: CompletableDeferred<Unit> = CompletableDeferred()

        application(configuration = configuration) {
            val observerJob = launch {
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = observerId, userName = "observer-$observerId"))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == observerId }
                    observerReady.complete(value = Unit)

                    receiveResponse<UpdateUsersResponse> { response: UpdateUsersResponse ->
                        response.users.any { user ->
                            user.id == actorId && user.name == actorUpdatedName
                        }
                    }.apply {
                        assertTrue(actual = users.any { user ->
                            user.id == actorId && user.name == actorUpdatedName
                        })
                        assertFalse(actual = users.any { user ->
                            user.id == actorId && user.name == actorInitialName
                        })
                    }
                }
            }

            val actorJob = launch {
                observerReady.await()
                client {
                    sendRequest(request = UserAuthorizeRequest(userId = actorId, userName = actorInitialName))
                    receiveResponse<UserAuthorizeResponse.Success> { response -> response.user.id == actorId }

                    sendRequest(request = UserChangeNameRequest(userName = actorUpdatedName))
                    receiveResponse<UserChangeNameResponse.Success> { response: UserChangeNameResponse.Success ->
                        response.user.id == actorId && response.user.name == actorUpdatedName
                    }
                }
            }

            joinAll(observerJob, actorJob)
        }
    }
}
