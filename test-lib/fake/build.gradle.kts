import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.mannodermaus.android-junit")
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

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    api(project(":lib-api"))
    api(AndroidX.test.runner)
    api(AndroidX.test.coreKtx)
    api(Testing.junit.jupiter.api)
    api(Koin.test)

    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.ui)
}
