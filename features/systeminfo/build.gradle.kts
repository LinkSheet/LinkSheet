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
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "fe.linksheet.feature.systeminfo"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    implementation(project(":util"))
    compileOnly(project(":hidden-api"))

    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.process.core)

    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.process)

    implementation(platform(Grrfe.gsonExt.bom))
    implementation(Grrfe.gsonExt.core)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
