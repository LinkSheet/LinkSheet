import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.buildlogic.Version
import fe.buildlogic.common.OptIn
import fe.buildlogic.common.extension.addOptIn

plugins {
    kotlin("android")
    id("com.android.library")
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "app.linksheet.feature.wiki"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        addOptIn(OptIn.ExperimentalTime)
    }

    room {
        schemaDirectory("$projectDir/schemas")
        generateKotlin = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":util"))
    implementation(project(":api"))
    implementation(AndroidX.room.runtime)
    implementation(AndroidX.room.ktx)
    ksp(AndroidX.room.compiler)

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.java)

    implementation(Grrfe.httpkt.core)
    implementation(Koin.android)

    implementation(AndroidX.core.ktx)

    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(project(":test-core"))
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
