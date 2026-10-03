package org.alexcawl.contract.users

import org.alexcawl.contract.users.proto.AuthorizeUserRequest as ProtoAuthorizeUserRequest
import org.alexcawl.contract.users.proto.AuthorizeUserResponse as ProtoAuthorizeUserResponse
import org.alexcawl.contract.users.proto.UsersServiceGrpcKt

public class UsersServerProto(
    private val server: UsersServer,
) : UsersServiceGrpcKt.UsersServiceCoroutineImplBase() {

    override suspend fun authorizeUser(request: ProtoAuthorizeUserRequest): ProtoAuthorizeUserResponse {
        return server.onAuthorizeUser(request = request.toModel()).toProto()
    }
}
