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
    annotationProcessor("com.github.1fexd.HiddenApiRefinePlugin:annotation-processor:4.4.1")
    compileOnly("com.github.1fexd.HiddenApiRefinePlugin:annotation:4.4.1")
    compileOnly("org.jetbrains:annotations:_")
    compileOnly(AndroidX.annotation)
}
