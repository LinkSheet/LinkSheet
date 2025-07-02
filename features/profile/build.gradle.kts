import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "fe.linksheet.feature.profile"
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
}

dependencies {
    api(project(":hidden-api"))
    implementation(project(":util"))
    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.core.ktx)

    implementation("dev.rikka.tools.refine:runtime:_")
}
