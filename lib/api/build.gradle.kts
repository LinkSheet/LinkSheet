import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
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
    implementation("androidx.room3:room3-common:3.0.0-alpha03")
    implementation("androidx.room3:room3-sqlite-wrapper:3.0.0-alpha03")
    implementation(AndroidX.sqlite.ktx)
    implementation(AndroidX.core.ktx)
    implementation(Koin.android)
}
