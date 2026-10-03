package org.alexcawl.sockets.contract.request

import kotlinx.serialization.Serializable

@Serializable
public data class UserChangeNameRequest(val userName: String) : ClientRequest
