
import app.linksheet.buildsrc.FlavorDimensions
import app.linksheet.buildsrc.ProductFlavors
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk

plugins {
    id("com.android.test")
}

android {
    namespace = "app.linksheet.test.e2e"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK
        targetSdk = AndroidSdk.COMPILE_SDK
        testApplicationId = "app.linksheet.test.e2e"
        targetProjectPath = ":app"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    flavorDimensions += FlavorDimensions.TYPE

    productFlavors {
        register(ProductFlavors.FOSS) {
            dimension = FlavorDimensions.TYPE
        }

        register(ProductFlavors.PRO) {
            dimension = FlavorDimensions.TYPE
        }
    }
}

dependencies {
    implementation(project(":test-instrument"))
    implementation(project(":lib-api"))
    implementation(AndroidX.core)
    implementation(AndroidX.test.runner)
    implementation(Testing.junit.jupiter.api)
    implementation("com.willowtreeapps.assertk:assertk:_")

    implementation("de.mannodermaus.junit5:android-test-core-junit6:2.0.1")
    implementation("de.mannodermaus.junit5:android-test-compose-junit6:2.0.1")
    implementation("de.mannodermaus.junit5:android-test-runner-junit6:2.0.1")
}
