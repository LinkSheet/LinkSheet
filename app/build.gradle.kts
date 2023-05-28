plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "fe.linksheet"
    compileSdk = 33

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = 25
        targetSdk = 33
        versionCode = 31
        versionName = "0.0.31"
        setProperty("archivesBaseName", "LinkSheet-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "LinkSheet Debug")
        }

        release {
            isMinifyEnabled = true
            resValue("string", "app_name", "LinkSheet")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module")
        }
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-process:2.6.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.2")

    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.21")

    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    implementation("androidx.webkit:webkit:1.7.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.material3:material3:1.2.0-alpha02")
    implementation("androidx.compose.material:material:1.4.3")

    implementation("androidx.compose.material:material-icons-extended:1.4.3")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    implementation("androidx.browser:browser:1.5.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.gitlab.grrfe.httpkt:core:13.0.0-alpha.32")
    implementation("com.gitlab.grrfe.httpkt:ext-gson:13.0.0-alpha.32")
    implementation("com.gitlab.grrfe.httpkt:core-android:13.0.0-alpha.32")
    implementation("com.gitlab.grrfe:GSONKtExtensions:2.4.0")
    implementation("com.github.1fexd:clearurlkt:0.0.17")
    implementation("com.github.1fexd:fastforwardkt:0.0.13")
    implementation("com.github.1fexd:libredirectkt:0.0.15")
    implementation("com.github.1fexd:mimetypekt:0.0.4")
    implementation("com.github.1fexd:stringbuilder-util-kt:1.0.1")
    implementation("com.github.1fexd.android-pref-helper:android-pref-helper:0.0.7")
    implementation("com.github.1fexd.android-pref-helper:android-pref-helper-compose:0.0.7")
    implementation("com.github.1fexd:compose-route-util:0.0.4")

    implementation("me.omico.lux:lux-androidx-compose-material3-pullrefresh")

    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.3")
}