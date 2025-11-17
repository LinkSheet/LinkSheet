import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies.MozillaComponents
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    kotlin("android")
    id("com.android.library")
    id("com.gitlab.grrfe.new-build-logic-plugin")
    id("kotlin-parcelize")
}
android {
    namespace = "app.linksheet.sdk.common"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }
    kotlin {
        jvmToolchain(Version.JVM)
    }
}

dependencies {
    api(KotlinX.coroutines.android)
    implementation(Koin.android)
    implementation(Grrfe.std.result.core)
    implementation(Grrfe.std.uri)
//    implementation(_1fexd.composeKit.core)
//    implementation(_1fexd.composeKit.koin)
//    implementation(_1fexd.composeKit.compose.core)
//    implementation(Square.okHttp3.android)
//    implementation(JetBrains.ktor.client.core)
//    implementation("com.github.seancfoley:ipaddress:_")
    implementation(AndroidX.core.ktx)


    testImplementation(Testing.robolectric)
    testImplementation(KotlinX.coroutines.test)
    testImplementation(AndroidX.test.ext.junit.ktx)
    testImplementation(Grrfe.std.test)
    testImplementation(Grrfe.std.result.assert)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
}
