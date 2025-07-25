import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.compose")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

group = "fe.linksheet.bottom.sheet.next"

android {
    namespace = group.toString()
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}

dependencies {
    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation("androidx.compose.material3:material3-android:_")

    implementation(AndroidX.activity.compose)
}
