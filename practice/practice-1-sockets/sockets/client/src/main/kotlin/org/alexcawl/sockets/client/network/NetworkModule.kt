package org.alexcawl.sockets.client.network

import kotlinx.coroutines.CoroutineScope
import org.alexcawl.sockets.common.Container

internal class NetworkModule(
    private val networkScope: CoroutineScope,
    private val timeoutMs: Int,
) : Container {

    val network: TcpNetwork by single {
        TcpNetworkImpl(
            coroutineScope = networkScope,
            timeoutMs = timeoutMs,
        )
    }
}
