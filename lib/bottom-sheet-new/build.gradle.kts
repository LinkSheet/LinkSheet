import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd

plugins {
    id("com.android.library")
    kotlin("plugin.compose")
    id("com.gitlab.grrfe.android-build-plugin")
}

group = "fe.linksheet.bottom.sheet.next"

android {
    namespace = group.toString()
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk
    compileSdkMinor = app.linksheet.buildsrc.Sdk.CompileSdkMinor

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
    implementation(_1fexd.composeKit.ext.mozillaSupportBase)
    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.Lifecycle.viewModelCompose)
    implementation("androidx.compose.material3:material3-android:_")

    implementation(AndroidX.activity.compose)
}
