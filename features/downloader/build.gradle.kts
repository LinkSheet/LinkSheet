import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version
import fe.buildlogic.common.OptIn
import fe.buildlogic.common.extension.addOptIn

plugins {
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "app.linksheet.feature.downloader"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        addOptIn(OptIn.ExperimentalTime)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":util"))
    implementation(project(":api"))

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.httpkt.core)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(AndroidX.core.ktx)

    testImplementation(Testing.robolectric)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
