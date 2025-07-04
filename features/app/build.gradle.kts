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
    namespace = "fe.linksheet.feature.app"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        addCompilerOptions(CompilerOption.WhenGuards)
        addPluginOptions(PluginOption.Parcelize.ExperimentalCodeGeneration to true)
    }

    packaging {
        resources {
            excludes += setOf("META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module", "META-INF/*.md")
        }
    }
}

dependencies {
    implementation(project(":util"))
    implementation(project(":test-fake"))
    implementation(AndroidX.compose.ui.graphics)
    testImplementation(Testing.robolectric)
    compileOnly(project(":hidden-api"))

    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.result.core)

    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.process)
    implementation(_1fexd.composeKit.compose.core)

    implementation(platform(Grrfe.gsonExt.bom))
    implementation(Grrfe.gsonExt.core)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
