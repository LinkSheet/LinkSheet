import com.android.build.api.dsl.AndroidLibrarySourceSet
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import fe.build.dependencies._1fexd

plugins {
    id("com.android.library")
    id("com.gitlab.grrfe.android-build-plugin")
}

android {
    namespace = "fe.linksheet.lib.common"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
    }

    kotlin {
        jvmToolchain(Version.JVM)
    }

    // Need to cast to com.android.build.gradle.dsl.AndroidLibrarySourceSet, otherwise the following exception is thrown:
//    class com.android.build.gradle.internal.api.DefaultAndroidLibrarySourceSet_Decorated cannot be cast to class com.android.build.gradle.api.AndroidLibrarySourceSet (com.android.build.gradle.internal.api.DefaultAndroidLibrarySourceSet_Decorated and com.android.build.gradle.api.AndroidLibrarySourceSet are in unnamed module of loader org.gradle.internal.classloader.VisitableURLClassLoader$InstrumentingVisitableURLClassLoader @2a7ab924)
    (sourceSets["main"] as AndroidLibrarySourceSet).kotlin.directories.add("src/main/compat")
}

dependencies {
    implementation(_1fexd.composeKit.core)
    implementation(AndroidX.core.ktx)
}
