import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.common.CompilerOption
import com.gitlab.grrfe.gradlebuild.extension.addCompilerOptions
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("androidx.room3")
    id("com.google.devtools.ksp")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "app.linksheet.feature.engine"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
        compilerOptions.freeCompilerArgs.addCompilerOptions(CompilerOption.ContextParameters)
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")

}

dependencies {
    implementation(project(":lib-common"))
    implementation(project(":lib-util"))
    implementation(project(":lib-api"))
    implementation(project(":lib-log"))
    implementation(project(":lib-compose"))
    implementation(project(":feature-app"))
    implementation(project(":feature-browser"))
    implementation(project(":feature-downloader"))
    implementation(project(":feature-libredirect"))
    implementation(project(":integration-clearurl"))
    implementation(project(":integration-embed-resolve"))
    implementation(project(":integration-amp2html"))
    implementation(project(":sdk-common"))

    implementation("androidx.room3:room3-runtime:_")
    ksp("androidx.room3:room3-compiler:_")


    implementation("sh.calvin.reorderable:reorderable:_")
    implementation("org.jsoup:jsoup:_")
    implementation("me.saket.unfurl:unfurl:_")
    implementation(_1fexd.fastForward)
    implementation(AndroidX.lifecycle.viewModel)
    implementation(AndroidX.room.common)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.navigation.compose)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.app)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.gsonExt.core)
    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.logging)
    implementation(JetBrains.ktor.client.contentNegotiation)
    implementation(JetBrains.ktor.client.json)
    implementation(JetBrains.ktor.client.encoding)
    implementation(JetBrains.ktor.plugins.serialization.gson)
    implementation("io.ktor:ktor-client-okhttp-jvm:_")
    implementation(KotlinX.coroutines.android)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.sqlite.ktx)

    implementation(KotlinX.serialization.json)
    implementation(KotlinX.serialization.protobuf)
    implementation(KotlinX.serialization.cbor)


    testImplementation(project(":test-fake"))
    testImplementation(project(":test-core"))
    testImplementation(JetBrains.ktor.client.mock)
    testImplementation(Testing.robolectric)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
