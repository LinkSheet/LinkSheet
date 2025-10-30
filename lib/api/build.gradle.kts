import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "app.linksheet.lib.api"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(AndroidX.room.common)
    implementation(AndroidX.sqlite.ktx)
    implementation(AndroidX.core.ktx)
    implementation(Koin.android)
    implementation("androidx.room:room-ktx:2.8.3")
}
