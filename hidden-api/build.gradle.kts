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
    compileOptions {
}

dependencies {
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:_")
    compileOnly("dev.rikka.tools.refine:annotation:_")
    compileOnly("org.jetbrains:annotations:_")
    compileOnly(AndroidX.annotation)
}
