import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
}

group = "app.linksheet.testlib.koin"

android {
    namespace = group.toString()
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(Koin.core)
    api(Koin.test)
    api("org.junit.jupiter:junit-jupiter-api:6.1.0")
}
