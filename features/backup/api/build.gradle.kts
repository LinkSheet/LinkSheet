
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.mannodermaus.android-junit")
    id("kotlin-parcelize")
}

android {
    namespace = "app.linksheet.feature.backup.api"
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":lib-api"))
    implementation(project(":lib-compose"))
    implementation(_1fexd.composeKit.ext.mozillaSupportBase)
    implementation(project(":lib-util"))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)
    implementation(AndroidX.core.ktx)

    androidTestImplementation(AndroidX.test.uiAutomator)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(platform("org.junit:junit-bom:5.14.1"))
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api")
    androidTestImplementation(AndroidX.compose.ui.test)
    androidTestImplementation("io.mockk:mockk-android:1.14.9")
    androidTestImplementation(project(":test-core"))

    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:_")
    testImplementation(Testing.junit4)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(JetBrains.ktor.client.mock)
    testImplementation(Testing.robolectric)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")

    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
