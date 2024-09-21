plugins {
//    alias(libs.plugins.android.test)
//    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-parcelize")
//    id("net.nemerosa.versioning")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "fe.linksheet.benchmark"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34


        missingDimensionStrategy("type", "foss")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

//    flavorDimensions += listOf("type")
//    productFlavors {
//        create("foss") { dimension = "type" }
//        create("pro") { dimension = "type" }
//    }


    kotlin {
        jvmToolchain(17)
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.junit)
    implementation(libs.espresso.core)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}
