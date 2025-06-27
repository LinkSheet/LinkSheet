import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd
import fe.buildlogic.Version
import fe.buildlogic.common.CompilerOption
import fe.buildlogic.common.PluginOption
import fe.buildlogic.common.extension.addCompilerOptions
import fe.buildlogic.common.extension.addPluginOptions

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "fe.linksheet.feature.profile"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    compileOnly(project(":hidden-api"))
    implementation(project(":util"))
    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.core.ktx)

    implementation("dev.rikka.tools.refine:runtime:_")
}
