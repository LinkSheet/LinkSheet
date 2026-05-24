import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
}
group = "fe.linksheet.hiddenapi"

android {
    namespace = group.toString()
    compileSdk = app.linksheet.buildsrc.Sdk.CompileSdk

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }
}

dependencies {
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:_")
    compileOnly("dev.rikka.tools.refine:annotation:_")
    compileOnly("org.jetbrains:annotations:_")
    compileOnly(AndroidX.annotation)
}
