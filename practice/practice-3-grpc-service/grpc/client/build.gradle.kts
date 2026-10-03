import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kotea"))
    implementation(project(":contract"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.kotlinx.serialization.properties)

    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(libs.compose.material3)
    implementation(libs.compose.components.resources)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation)

    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.java.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.kotlin)
    runtimeOnly(libs.grpc.netty)
}

kotlin {
    explicitApi()
}

compose.desktop {
    application {
        mainClass = "org.alexcawl.client.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GrpcSocialClient"
            packageVersion = "1.0.0"
        }
    }
}
