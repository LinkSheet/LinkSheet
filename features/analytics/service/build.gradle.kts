
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
}

android {
    namespace = "app.linksheet.feature.analytics"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK

        val localProviders = withProviders(rootProject.propertiesProvider(LocalPropertiesFile), SystemEnvironment)
        buildConfig {
            boolean(
                "ANALYTICS_SUPPORTED",
                localProviders.get("ANALYTICS_SUPPORTED")?.toBooleanStrictOrNull() != false
            )
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
    implementation(project(":lib-util"))
    implementation(project(":lib-log"))
    implementation(project(":lib-compose"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)

    implementation(Grrfe.std.core)

    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.lifecycle.network.core)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation("io.viascom.nanoid:nanoid:_")

    implementation(Grrfe.gsonExt.core)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
