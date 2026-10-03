package org.alexcawl.reverse

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.alexcawl.common.decode
import org.alexcawl.common.logger
import org.alexcawl.common.resourcesProperties
import java.util.concurrent.TimeUnit

fun main() {
    val configuration: ReverseClientConfiguration = resourcesProperties(name = "reverse.properties").decode()
    runBlocking {
        ReverseClient(
            host = configuration.host,
            port = configuration.port,
        ).use { client: ReverseClient ->
            client.run()
        }
    }
}

private class ReverseClient(
    host: String,
    port: Int,
) : AutoCloseable {

    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()

    private val stub = ReverseServiceGrpcKt.ReverseServiceCoroutineStub(channel = channel)

    suspend fun run() {
        logger.info("Connected to reverse service at ${channel.authority()}.")
        logger.info("Type text and press Enter. Type 'exit' to quit.")

        while (true) {
            print("> ")
            val input: String = readlnOrNull()?.trim() ?: break
            if (input.equals("exit", ignoreCase = true)) break
            if (input.isEmpty()) continue

            val response = stub.reverseString(
                ReverseRequest.newBuilder()
                    .setText(input)
                    .build()
            )

            println("Reverse response: '${response.reversedText}'")
        }
    }

    override fun close() {
        channel.shutdown()
        channel.awaitTermination(5, TimeUnit.SECONDS)
    }
}
