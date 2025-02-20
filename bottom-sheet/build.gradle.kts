import fe.buildlogic.dependency.PinnedVersions
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("build-logic-plugin")
}

group = "fe.linksheet.bottom.sheet"

android {
    namespace = group.toString()
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.ui.withVersion(PinnedVersions.ComposeVersion))
    implementation(PinnedVersions.Material3)
    implementation(AndroidX.Lifecycle.viewModelCompose)
}
