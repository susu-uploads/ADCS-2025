package org.alexcawl.sockets.server.testkit

import kotlinx.coroutines.test.TestScope
import org.alexcawl.sockets.contract.Contract

internal interface ApplicationTestScope {

    suspend fun TestScope.client(
        clientFormat: Contract.ClientFormat = Contract.clientJsonFormat,
        block: suspend ClientTestScope.() -> Unit,
    )
}
