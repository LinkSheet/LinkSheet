import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}
group = "fe.linksheet.hiddenapi"

android {
    namespace = group.toString()
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:_")
    compileOnly("dev.rikka.tools.refine:annotation:_")
    compileOnly("org.jetbrains:annotations:_")
    compileOnly(AndroidX.annotation)
}
