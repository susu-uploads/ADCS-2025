plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    application
}

dependencies {
    implementation(project(":common"))

    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.java.stub)
    runtimeOnly(libs.grpc.netty)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.properties)

    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

application {
    mainClass.set("org.alexcawl.reverse.ReverseServerKt")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.32.0"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.75.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
                create("grpckt")
            }
        }
    }
}

tasks.register<JavaExec>("runReverseServer") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.alexcawl.reverse.ReverseServerKt")
}

tasks.register<JavaExec>("runReverseClient") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.alexcawl.reverse.ReverseClientKt")
}
