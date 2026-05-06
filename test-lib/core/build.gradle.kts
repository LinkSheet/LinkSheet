
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.common.CompilerOption
import com.gitlab.grrfe.gradlebuild.extension.addCompilerOptions

plugins {
    id("com.android.library")
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
        jvmToolchain(Version.JVM)
        compilerOptions.freeCompilerArgs.addCompilerOptions(CompilerOption.AllowKotlinPackage)
    }

    buildFeatures {
        buildConfig = true
    }
}


dependencies {
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)
    implementation("androidx.room3:room3-runtime:3.0.0-alpha03")
}
