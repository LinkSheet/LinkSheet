import fe.buildsrc.Version

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val group = "fe.linksheet.config"

android {
    namespace = group
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}
