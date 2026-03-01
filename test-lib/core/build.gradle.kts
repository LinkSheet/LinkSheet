import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.common.CompilerOption
import com.gitlab.grrfe.gradlebuild.extension.addCompilerOptions

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.android-build-plugin")
}

group = "fe.linksheet.testlib.core"

android {
    namespace = group.toString()
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(com.gitlab.grrfe.gradlebuild.Version.JVM)
        addCompilerOptions(CompilerOption.AllowKotlinPackage)
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
