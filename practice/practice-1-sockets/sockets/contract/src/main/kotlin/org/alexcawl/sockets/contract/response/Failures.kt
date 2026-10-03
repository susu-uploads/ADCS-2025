package org.alexcawl.sockets.contract.response

import kotlinx.serialization.Serializable

@Serializable
public data object NotAuthorizedResponse : ServerResponse

@Serializable
public data class FailureResponse(val message: String) : ServerResponse

public fun Throwable.asResponse(): FailureResponse = FailureResponse(message = stackTraceToString())
