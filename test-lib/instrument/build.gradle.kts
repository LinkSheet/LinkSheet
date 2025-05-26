import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.build-logic-plugin")
}

group = "fe.linksheet.testlib.ui"

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
    api(project(":test-core"))

    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.test)
    implementation(AndroidX.compose.ui.testJunit4)

    implementation(AndroidX.test.ext.junit.ktx)
    api("androidx.test.uiautomator:uiautomator:2.3.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.core.ktx)
}
