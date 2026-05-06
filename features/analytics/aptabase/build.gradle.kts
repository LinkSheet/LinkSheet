
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
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "app.linksheet.feature.analytics.aptabase"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK

        val localProviders = withProviders(rootProject.propertiesProvider(LocalPropertiesFile), SystemEnvironment)
        buildConfig {
            string("APTABASE_API_KEY", localProviders.get("APTABASE_API_KEY"))
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
    implementation(project(":feature-analytics-service"))
    implementation(project(":lib-api"))
    implementation(project(":lib-log"))
    implementation(project(":lib-http"))

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.java)

    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core2)

    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.serialization.gson)

    implementation(Grrfe.gsonExt.core)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
