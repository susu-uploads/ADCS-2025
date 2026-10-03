package org.alexcawl.contract.users

import org.alexcawl.contract.users.proto.UsersServiceGrpcKt

public class UsersClient(
    private val stub: UsersServiceGrpcKt.UsersServiceCoroutineStub,
) {

    public suspend fun authorizeUser(request: AuthorizeUserRequest): AuthorizeUserResponse {
        return stub.authorizeUser(request = request.toProto()).toModel()
    }
}
