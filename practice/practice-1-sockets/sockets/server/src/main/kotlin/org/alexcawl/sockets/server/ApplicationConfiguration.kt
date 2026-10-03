package org.alexcawl.sockets.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationConfiguration(
    @SerialName(value = "server.port")
    val port: Int = 8080,
    @SerialName(value = "server.verbose")
    val isVerbose: Boolean = true,
    @SerialName(value = "server.echo")
    val isEcho: Boolean = false,
    @SerialName(value = "server.acceptTimeout")
    val acceptTimeoutMs: Int = 3_000,
    @SerialName(value = "database.url")
    val databaseUrl: String = "jdbc:h2:mem:sockets-practice;DB_CLOSE_DELAY=-1;",
    @SerialName(value = "database.driver")
    val databaseDriver: String = "org.h2.Driver",
    @SerialName(value = "database.user")
    val databaseUser: String? = null,
    @SerialName(value = "database.password")
    val databasePassword: String? = null,
    @SerialName(value = "chat.systemUserName")
    val systemUserName: String = "Server",
    @SerialName(value = "chat.systemChatName")
    val systemChatName: String = "System",
)
