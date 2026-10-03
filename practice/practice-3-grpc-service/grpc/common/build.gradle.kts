plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.kotlinx.serialization.properties)
}
