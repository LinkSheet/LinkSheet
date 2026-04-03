import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.library-build-plugin")
}

android {
    namespace = "app.linksheet.lib.log"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    implementation(Grrfe.std.core)
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.core.ktx)
}
