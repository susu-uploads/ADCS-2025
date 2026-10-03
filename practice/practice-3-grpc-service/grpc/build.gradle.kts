plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.protobuf) apply false
}

allprojects {
    group = "org.alexcawl.grpc"
    version = "1.0.0"
}
