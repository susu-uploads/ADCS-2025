package org.alexcawl.contract.users

import org.alexcawl.contract.entity.Failure
import org.alexcawl.contract.entity.toModel
import org.alexcawl.contract.entity.toProto
import org.alexcawl.contract.users.proto.authorizeUserRequest
import org.alexcawl.contract.users.proto.AuthorizeUserRequest as ProtoAuthorizeUserRequest
import org.alexcawl.contract.users.proto.AuthorizeUserResponse as ProtoAuthorizeUserResponse
import org.alexcawl.contract.users.proto.authorizeUserResponse

internal fun AuthorizeUserRequest.toProto(): ProtoAuthorizeUserRequest = authorizeUserRequest {
    name = this@toProto.name
}

internal fun ProtoAuthorizeUserRequest.toModel(): AuthorizeUserRequest {
    return AuthorizeUserRequest(
        name = name,
    )
}

internal fun AuthorizeUserResponse.toProto(): ProtoAuthorizeUserResponse = authorizeUserResponse {
    when (this@toProto) {
        is AuthorizeUserResponse.Success -> {
            user = this@toProto.user.toProto()
        }
        is AuthorizeUserResponse.ValidationFailure -> {
            validationFailure = Failure(message = this@toProto.message).toProto()
        }
    }
}

internal fun ProtoAuthorizeUserResponse.toModel(): AuthorizeUserResponse {
    return when (resultCase) {
        ProtoAuthorizeUserResponse.ResultCase.USER -> AuthorizeUserResponse.Success(user = user.toModel())
        ProtoAuthorizeUserResponse.ResultCase.VALIDATION_FAILURE -> {
            AuthorizeUserResponse.ValidationFailure(message = validationFailure.message)
        }
        ProtoAuthorizeUserResponse.ResultCase.RESULT_NOT_SET -> {
            AuthorizeUserResponse.ValidationFailure(message = "AuthorizeUser returned no result.")
        }
    }
}
