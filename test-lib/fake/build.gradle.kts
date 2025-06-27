import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
    id("de.mannodermaus.android-junit5")
}

group = "fe.linksheet.testlib.fake"

android {
    namespace = group.toString()
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)

    implementation(AndroidX.compose.ui)
}
