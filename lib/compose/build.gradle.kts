import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildlogic.Version

plugins {
    kotlin("android")
    kotlin("plugin.compose")
    id("com.android.library")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

android {
    namespace = "fe.linksheet.lib.compose"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += setOf("META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module", "META-INF/*.md")
        }
    }
}

dependencies {
    implementation(platform(Grrfe.std.bom))
    implementation(platform(_1fexd.composeKit.bom))

    implementation(Grrfe.std.result.core)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.layout)

    implementation(AndroidX.core.ktx)

    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.text)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)

    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
}
