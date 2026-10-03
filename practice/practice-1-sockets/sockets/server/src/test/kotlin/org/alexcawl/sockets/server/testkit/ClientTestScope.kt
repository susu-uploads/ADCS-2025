package org.alexcawl.sockets.server.testkit

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.SharedFlow
import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.contract.response.ServerResponse
import kotlin.reflect.KClass

internal interface ClientTestScope {

    suspend fun sendRequest(request: ClientRequest)

    suspend fun sendRequest(vararg requests: ClientRequest)

    val responses: ReceiveChannel<ServerResponse>

    suspend fun <SR : ServerResponse> receiveResponse(type: KClass<SR>, condition: (response: SR) -> Boolean): SR

    suspend fun <SR : ServerResponse> receiveResponse(type: KClass<SR>): SR
}

internal suspend inline fun <reified SR : ServerResponse> ClientTestScope.receiveResponse(noinline condition: (response: SR) -> Boolean): SR {
    return receiveResponse(type = SR::class, condition = condition)
}

internal suspend inline fun <reified SR : ServerResponse> ClientTestScope.receiveResponse(): SR {
    return receiveResponse(type = SR::class)
}
