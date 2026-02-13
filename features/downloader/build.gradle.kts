
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
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
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":util"))
    implementation(project(":api"))
    implementation(project(":integration-mime-types"))

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.core2.core)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(AndroidX.core.ktx)

    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.logging)
    implementation(JetBrains.ktor.client.contentNegotiation)
    implementation(JetBrains.ktor.client.json)
    implementation(JetBrains.ktor.client.encoding)
    implementation(JetBrains.ktor.plugins.serialization.gson)
    testImplementation(JetBrains.ktor.client.mock)


    testImplementation(Grrfe.httpkt.core2.test)
    testImplementation(Testing.robolectric)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
