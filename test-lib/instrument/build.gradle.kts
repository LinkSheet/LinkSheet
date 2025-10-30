import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

group = "fe.linksheet.testlib.ui"

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
    api(project(":test-core"))

    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.test)
    implementation(AndroidX.compose.ui.testJunit4)

    implementation(AndroidX.test.ext.junit.ktx)
    api(AndroidX.test.uiAutomator)
    implementation(AndroidX.activity.ktx)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.core.ktx)
}
