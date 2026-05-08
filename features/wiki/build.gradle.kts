import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("androidx.room3")
    id("com.google.devtools.ksp")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "app.linksheet.feature.wiki"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":lib-api"))
    implementation(project(":lib-util"))
    implementation(project(":lib-common"))
    implementation(project(":lib-compose"))

    implementation("androidx.room3:room3-runtime:_")
    ksp("androidx.room3:room3-compiler:_")

    implementation(AndroidX.core.ktx)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.compose.component)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.result.core)
    implementation("com.github.jeziellago:compose-markdown:_")
    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.logging)
    implementation(JetBrains.ktor.client.contentNegotiation)
    implementation(JetBrains.ktor.client.json)
    implementation(JetBrains.ktor.client.encoding)
    implementation(JetBrains.ktor.plugins.serialization.gson)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
