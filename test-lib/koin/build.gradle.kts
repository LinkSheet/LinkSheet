import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

group = "app.linksheet.testlib.koin"

android {
    namespace = group.toString()
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(AndroidX.room.ktx)
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)
}
