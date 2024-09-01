import de.fayard.refreshVersions.core.versionFor
import fe.buildsrc.Version
import fe.buildsrc.dependency.PinnedVersions

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val group = "fe.linksheet.bottom.sheet"

android {
    namespace = group
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.compiler)
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    implementation(platform(AndroidX.compose.bom))
    implementation(PinnedVersions.ComposeUi)
    implementation(PinnedVersions.Material3)
    implementation(AndroidX.Lifecycle.viewModelCompose)
}
