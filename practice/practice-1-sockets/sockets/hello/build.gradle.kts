plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(project(":common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.properties)
}

application {
    mainClass.set("org.alexcawl.sockets.hello.HelloServerKt")
}

tasks.register<JavaExec>("runHelloServer") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.alexcawl.sockets.hello.HelloServerKt")
}

tasks.register<JavaExec>("runHelloClient") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.alexcawl.sockets.hello.HelloClientKt")
}
