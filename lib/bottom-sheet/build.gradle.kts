import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd

plugins {
    id("com.android.library")
    kotlin("plugin.compose")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "app.linksheet.lib.bottomsheet"
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

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
    implementation(_1fexd.composeKit.ext.mozillaSupportBase)
    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.Lifecycle.viewModelCompose)
}
