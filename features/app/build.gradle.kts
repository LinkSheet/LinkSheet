import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version
import fe.buildlogic.common.CompilerOption
import fe.buildlogic.common.PluginOption
import fe.buildlogic.common.extension.addCompilerOptions
import fe.buildlogic.common.extension.addPluginOptions

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "app.linksheet.feature.app"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        addCompilerOptions(CompilerOption.WhenGuards)
        addPluginOptions(PluginOption.Parcelize.ExperimentalCodeGeneration to true)
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":util"))
    implementation(project(":test-fake"))
    implementation(AndroidX.compose.ui.graphics)
    testImplementation(Testing.robolectric)
    compileOnly(project(":hidden-api"))

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)

    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.process)
    implementation(_1fexd.composeKit.compose.core)

    implementation(Grrfe.gsonExt.core)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
