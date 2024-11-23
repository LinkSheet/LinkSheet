import fe.buildlogic.Version

plugins {
    id("com.android.library")
    id("build-logic-plugin")
}
group = "fe.linksheet.hiddenapi"

android {
    namespace = group.toString()
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
    }
}

dependencies {
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:4.0.0")
    compileOnly("dev.rikka.tools.refine:annotation:4.0.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("androidx.annotation:annotation:1.8.0")
}
