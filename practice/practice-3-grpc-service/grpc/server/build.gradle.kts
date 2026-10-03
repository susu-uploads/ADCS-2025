plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(project(":contract"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.properties)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    runtimeOnly(libs.h2)
    runtimeOnly(libs.grpc.netty)
}

kotlin {
    explicitApi()
}

application {
    mainClass.set("org.alexcawl.server.MainKt")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
