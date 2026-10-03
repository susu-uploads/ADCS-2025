package org.alexcawl.server.controller

import io.grpc.Server
import io.grpc.ServerBuilder
import org.alexcawl.common.Container
import org.alexcawl.contract.interactions.InteractionsServer
import org.alexcawl.contract.interactions.InteractionsServerProto
import org.alexcawl.contract.posts.PostsServer
import org.alexcawl.contract.posts.PostsServerProto
import org.alexcawl.contract.users.UsersServer
import org.alexcawl.contract.users.UsersServerProto
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase
import org.alexcawl.server.domain.usecase.CommentPostUseCase
import org.alexcawl.server.domain.usecase.CreatePostUseCase
import org.alexcawl.server.domain.usecase.DislikePostUseCase
import org.alexcawl.server.domain.usecase.LikePostUseCase
import org.alexcawl.server.domain.usecase.ListFeedUseCase

internal class ControllerModule(
    private val port: Int,
    private val authorizeUserUseCase: AuthorizeUserUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val listFeedUseCase: ListFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val dislikePostUseCase: DislikePostUseCase,
    private val commentPostUseCase: CommentPostUseCase,
) : Container {

    private val usersServer: UsersServer by single {
        UsersServerImpl(
            authorizeUserUseCase = authorizeUserUseCase,
        )
    }

    private val postsServer: PostsServer by single {
        PostsServerImpl(
            createPostUseCase = createPostUseCase,
            listFeedUseCase = listFeedUseCase,
        )
    }

    private val interactionsServer: InteractionsServer by single {
        InteractionsServerImpl(
            likePostUseCase = likePostUseCase,
            dislikePostUseCase = dislikePostUseCase,
            commentPostUseCase = commentPostUseCase,
        )
    }

    private val usersServerProto: UsersServerProto by single {
        UsersServerProto(server = usersServer)
    }

    private val postsServerProto: PostsServerProto by single {
        PostsServerProto(server = postsServer)
    }

    private val interactionsServerProto: InteractionsServerProto by single {
        InteractionsServerProto(server = interactionsServer)
    }

    val grpcServer: Server by single {
        ServerBuilder.forPort(port)
            .addService(usersServerProto)
            .addService(postsServerProto)
            .addService(interactionsServerProto)
            .build()
    }
}
