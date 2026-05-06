
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.common.PluginOption
import com.gitlab.grrfe.gradlebuild.extension.addPluginOptions
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "app.linksheet.feature.app"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        compilerOptions.freeCompilerArgs.addPluginOptions(PluginOption.Parcelize.ExperimentalCodeGeneration to true)
    }
}

dependencies {
    implementation(project(":lib-common"))
    implementation(project(":lib-compose"))
    implementation(project(":lib-util"))
    implementation(project(":test-fake"))
    compileOnly(project(":lib-hidden-api"))
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.foundation)

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)

    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.koin)
    implementation("com.github.LinkSheet.flavors:core:_")

    implementation(Grrfe.gsonExt.core)
    implementation("io.github.reandroid:ARSCLib:_")
    implementation(AndroidX.core.ktx)

    testImplementation(Testing.robolectric)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
