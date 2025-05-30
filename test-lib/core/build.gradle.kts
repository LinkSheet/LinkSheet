import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version
import fe.buildlogic.common.CompilerOption
import fe.buildlogic.common.extension.addCompilerOptions

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.build-logic-plugin")
    id("de.mannodermaus.android-junit5")
}

group = "fe.linksheet.testlib.core"

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
    addCompilerOptions(CompilerOption.AllowKotlinPackage)
}

dependencies {
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)
}
