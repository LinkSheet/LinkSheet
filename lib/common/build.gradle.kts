import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "fe.linksheet.lib.common"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    val main by sourceSets
    for (it in arrayOf("compat")) {
        main.java.srcDir("src/main/$it")
    }
}

dependencies {
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.core.ktx)
}
