package org.alexcawl.reverse

import io.grpc.Server
import io.grpc.ServerBuilder
import org.alexcawl.common.decode
import org.alexcawl.common.resourcesProperties

fun main() {
    val configuration: ReverseServerConfiguration = resourcesProperties(name = "reverse.properties").decode()
    val server: Server = ServerBuilder
        .forPort(configuration.port)
        .addService(ReverseGrpcService())
        .build()
    server.start()
    server.awaitTermination()
}
