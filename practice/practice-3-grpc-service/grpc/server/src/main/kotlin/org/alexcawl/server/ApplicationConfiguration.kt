package org.alexcawl.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApplicationConfiguration(
    @SerialName(value = "server.port")
    val port: Int = 50051,
    @SerialName(value = "database.url")
    val databaseUrl: String = "jdbc:h2:mem:grpc-practice;DB_CLOSE_DELAY=-1;",
    @SerialName(value = "database.driver")
    val databaseDriver: String = "org.h2.Driver",
    @SerialName(value = "database.user")
    val databaseUser: String? = null,
    @SerialName(value = "database.password")
    val databasePassword: String? = null,
)
