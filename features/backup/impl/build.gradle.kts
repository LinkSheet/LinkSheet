
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.mannodermaus.android-junit")
    id("kotlin-parcelize")
    id("de.infix.testBalloon") version "1.0.0-K2.4.0"
}

android {
    namespace = "app.linksheet.feature.backup.impl"
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
    api(project(":feature-backup-api"))
    implementation(project(":lib-api"))
    implementation(project(":lib-compose"))
    implementation(_1fexd.composeKit.ext.mozillaSupportBase)
    implementation(project(":lib-util"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Koin.workManager)
    implementation(Square.okio)
    implementation(KotlinX.datetime)
    implementation(KotlinX.serialization.json)
    implementation(KotlinX.serialization.json.okio)
    implementation(KotlinX.serialization.protobuf)
    implementation(KotlinX.serialization.cbor)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(Koin.workManager)
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)

    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.util)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.preference.compose.mock2)

    implementation(Grrfe.gsonExt.core)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.activity.compose)

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

    testImplementation("de.infix.testBalloon:testBalloon-framework-core:_")
    testImplementation("de.infix.testBalloon:testBalloon-integration-robolectric:_")
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
