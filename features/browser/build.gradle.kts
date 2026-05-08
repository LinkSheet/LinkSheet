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
    namespace = "app.linksheet.feature.browser"
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
    implementation(project(":feature-app"))

    implementation("androidx.room3:room3-runtime:_")
    ksp("androidx.room3:room3-compiler:_")

    implementation(AndroidX.core.ktx)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.preference.compose.mock2)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.compose.component)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
