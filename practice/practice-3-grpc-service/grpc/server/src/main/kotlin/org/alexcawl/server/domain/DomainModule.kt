package org.alexcawl.server.domain

import org.alexcawl.common.Container
import org.alexcawl.server.domain.usecase.AuthorizeUserUseCase
import org.alexcawl.server.domain.usecase.CommentPostUseCase
import org.alexcawl.server.domain.usecase.CreatePostUseCase
import org.alexcawl.server.domain.usecase.DislikePostUseCase
import org.alexcawl.server.domain.usecase.LikePostUseCase
import org.alexcawl.server.domain.usecase.ListFeedUseCase
import org.alexcawl.server.domain.usecase.impl.AuthorizeUserUseCaseImpl
import org.alexcawl.server.domain.usecase.impl.CommentPostUseCaseImpl
import org.alexcawl.server.domain.usecase.impl.CreatePostUseCaseImpl
import org.alexcawl.server.domain.usecase.impl.DislikePostUseCaseImpl
import org.alexcawl.server.domain.usecase.impl.LikePostUseCaseImpl
import org.alexcawl.server.domain.usecase.impl.ListFeedUseCaseImpl
import org.jetbrains.exposed.sql.Database

internal class DomainModule(
    private val database: Database,
) : Container {

    val authorizeUserUseCase: AuthorizeUserUseCase by single {
        AuthorizeUserUseCaseImpl(database = database)
    }

    val createPostUseCase: CreatePostUseCase by single {
        CreatePostUseCaseImpl(database = database)
    }

    val listFeedUseCase: ListFeedUseCase by single {
        ListFeedUseCaseImpl(database = database)
    }

    val likePostUseCase: LikePostUseCase by single {
        LikePostUseCaseImpl(database = database)
    }

    val dislikePostUseCase: DislikePostUseCase by single {
        DislikePostUseCaseImpl(database = database)
    }

    val commentPostUseCase: CommentPostUseCase by single {
        CommentPostUseCaseImpl(database = database)
    }
}
