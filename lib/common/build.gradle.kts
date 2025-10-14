import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
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

    packaging {
        resources {
            excludes += setOf("META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module", "META-INF/*.md")
        }
    }

    val main by sourceSets
    for (it in arrayOf("compat")) {
        main.java.srcDir("src/main/$it")
    }
}

dependencies {
    implementation(AndroidX.room.common)

    implementation(Grrfe.std.result.core)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)

    implementation(AndroidX.core.ktx)
}
