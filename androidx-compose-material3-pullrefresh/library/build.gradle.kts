plugins {
    id("com.android.library")
    kotlin("android")
}


android {
    namespace = "me.omico.lux.compose.material3.pullrefresh"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
//        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.compiler)
    }
}

dependencies {
    compileOnly(platform(AndroidX.compose.bom))
    compileOnly(AndroidX.compose.foundation)
    compileOnly(AndroidX.compose.material3)
    compileOnly(AndroidX.compose.ui)
}
