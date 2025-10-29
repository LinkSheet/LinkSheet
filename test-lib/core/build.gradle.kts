import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version
import fe.buildlogic.common.CompilerOption
import fe.buildlogic.common.extension.addCompilerOptions

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
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

    packaging {
        resources {
            pickFirsts += setOf(
                "META-INF/{AL2.0,LGPL2.1}",
                "META-INF/atomicfu.kotlin_module",
                "META-INF/*.md",
                "META-INF/**.MF"
            )
        }
    }
}

kotlin {
    jvmToolchain(Version.JVM)
    addCompilerOptions(CompilerOption.AllowKotlinPackage)
}

dependencies {
    implementation(AndroidX.room.ktx)
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)
}
