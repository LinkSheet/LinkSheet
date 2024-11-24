import fe.buildlogic.dependency.PinnedVersions
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("build-logic-plugin")
}

group = "fe.linksheet.scaffold"

android {
    namespace = group.toString()
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.ui.withVersion(PinnedVersions.ComposeVersion))
    implementation(PinnedVersions.Material3)
    implementation(AndroidX.compose.foundation)

    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.activity.compose)
}
