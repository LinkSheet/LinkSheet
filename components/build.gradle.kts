import de.fayard.refreshVersions.core.versionFor
import fe.buildsrc.PinnedVersions
import fe.buildsrc.Version
import fe.buildsrc._1fexd

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val group = "fe.linksheet.components"

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
    implementation(project(":compose-util"))

    implementation(platform(AndroidX.compose.bom))
    implementation(PinnedVersions.ComposeUi)
    implementation(PinnedVersions.Material3)

    implementation(AndroidX.compose.ui.toolingPreview)

    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)

    implementation(_1fexd.android.span.compose)
}
