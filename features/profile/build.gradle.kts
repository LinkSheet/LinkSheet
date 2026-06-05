import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
    id("dev.rikka.tools.refine")
}

android {
    namespace = "app.linksheet.feature.profile"
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    implementation(project(":feature-app"))
    implementation(project(":lib-api"))
    implementation(project(":lib-compose"))
    implementation(project(":lib-util"))
    compileOnly(project(":lib-hidden-api"))
    implementation("com.github.1fexd.HiddenApiRefinePlugin:runtime:4.4.1")

    implementation(_1fexd.composeKit.ext.mozillaSupportBase)
    implementation(AndroidX.core.ktx)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.preference.compose.mock2)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)

    testImplementation(Testing.robolectric)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
