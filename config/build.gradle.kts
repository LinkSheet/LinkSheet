import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
}

group = "fe.linksheet.config"

android {
    namespace = group.toString()
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }
}

kotlin {
    jvmToolchain(Version.JVM)
}
