import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "fe.linksheet.lib.util"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    implementation(AndroidX.core.ktx)
    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.compose.core)
}
