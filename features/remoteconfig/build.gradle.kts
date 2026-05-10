
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.android.extension.buildConfig
import com.gitlab.grrfe.gradlebuild.util.LocalPropertiesFile
import com.gitlab.grrfe.gradlebuild.util.SystemEnvironment
import com.gitlab.grrfe.gradlebuild.util.propertiesProvider
import com.gitlab.grrfe.gradlebuild.util.withProviders
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.mannodermaus.android-junit")
}

android {
    namespace = "app.linksheet.feature.remoteconfig"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK

        val localProviders = withProviders(rootProject.propertiesProvider(LocalPropertiesFile), SystemEnvironment)
        buildConfig {
            string("API_HOST", localProviders.get("API_HOST"))
        }
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
    implementation(project(":lib-common"))
    implementation(project(":lib-compose"))
    implementation(project(":lib-log"))
    implementation(project(":lib-util"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Koin.workManager)
    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.contentNegotiation)
    implementation(JetBrains.ktor.client.gson)

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)

    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.preference.compose.mock2)

    implementation(Grrfe.gsonExt.core)
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
