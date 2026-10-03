package org.alexcawl.sockets.contract

import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.alexcawl.sockets.contract.request.ClientRequest
import org.alexcawl.sockets.contract.response.ServerResponse

public object Contract {

    private val stringFormat: StringFormat by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    public val serverJsonFormat: ServerFormat by lazy {
        object : ServerFormat {
            override fun encode(value: ServerResponse): String = stringFormat.encodeToString(value = value)
            override fun decode(string: String): ClientRequest = stringFormat.decodeFromString(string = string)
        }
    }

    public val clientJsonFormat: ClientFormat by lazy {
        object : ClientFormat {
            override fun encode(value: ClientRequest): String = stringFormat.encodeToString(value = value)
            override fun decode(string: String): ServerResponse = stringFormat.decodeFromString(string = string)
        }
    }

    public interface ServerFormat {

        public fun encode(value: ServerResponse): String

        public fun decode(string: String): ClientRequest
    }

    public interface ClientFormat {

        public fun encode(value: ClientRequest): String

        public fun decode(string: String): ServerResponse
    }
}
