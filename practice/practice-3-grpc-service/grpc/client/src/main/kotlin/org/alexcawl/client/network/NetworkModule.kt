package org.alexcawl.client.network

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.alexcawl.common.Container
import org.alexcawl.contract.interactions.InteractionsClient
import org.alexcawl.contract.interactions.proto.InteractionsServiceGrpcKt
import org.alexcawl.contract.posts.PostsClient
import org.alexcawl.contract.posts.proto.PostsServiceGrpcKt
import org.alexcawl.contract.users.UsersClient
import org.alexcawl.contract.users.proto.UsersServiceGrpcKt
import java.util.concurrent.TimeUnit

internal class NetworkModule(
    val host: String,
    val port: Int,
) : Container, AutoCloseable {

    private val channel: ManagedChannel by single {
        ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build()
    }

    val usersClient: UsersClient by single {
        UsersClient(
            stub = UsersServiceGrpcKt.UsersServiceCoroutineStub(channel = channel),
        )
    }

    val postsClient: PostsClient by single {
        PostsClient(
            stub = PostsServiceGrpcKt.PostsServiceCoroutineStub(channel = channel),
        )
    }

    val interactionsClient: InteractionsClient by single {
        InteractionsClient(
            stub = InteractionsServiceGrpcKt.InteractionsServiceCoroutineStub(channel = channel),
        )
    }

    override fun close() {
        channel.shutdown()
    }
}
