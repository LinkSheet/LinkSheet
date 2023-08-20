import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.SCMInfo
import groovy.lang.Closure
import net.nemerosa.versioning.VersioningExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("net.nemerosa.versioning")
    id("com.google.devtools.ksp")
}

// Must be defined before the android block, or else it won't work
versioning {
    class KotlinClosure4<in T : Any?, in U : Any?, in V : Any?, in W : Any?, R : Any>(
        val function: (T, U, V, W) -> R?,
        owner: Any? = null,
        thisObject: Any? = null
    ) : Closure<R?>(owner, thisObject) {
        @Suppress("unused")
        fun doCall(t: T, u: U, v: V, w: W): R? = function(t, u, v, w)
    }

    releaseMode = KotlinClosure4<String?, String?, String?, VersioningExtension, String>({ _, _, currentTag, _ ->
        currentTag
    })

    releaseParser = KotlinClosure2<SCMInfo, String, ReleaseInfo>({ info, _ ->
        ReleaseInfo("release", info.tag)
    })
}

android {
    namespace = "fe.linksheet"
    compileSdk = 33

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = 25
        targetSdk = 33
        versionCode = versioning.info.tag?.let {
            versioning.info.versionNumber.versionCode
        } ?: (System.currentTimeMillis() / 1000).toInt()

        versionName = versioning.info.tag ?: versioning.info.full
        setProperty("archivesBaseName", "LinkSheet-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("env") {
            storeFile = File(System.getenv("KEYSTORE_FILE_PATH") ?: "")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_name", "LinkSheet")
        }

        register("nightly") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("env")

            applicationIdSuffix = ".nightly"
            versionNameSuffix = "-nightly"
            resValue("string", "app_name", "LinkSheet Nightly")
        }
    }

    flavorDimensions += listOf("type")

    productFlavors {
        create("foss") {
            dimension = "type"
        }

        create("pro") {
            dimension = "type"

            applicationIdSuffix = ".pro"
            versionName = "-pro"
            resValue("string", "app_name", "LinkSheet Pro")
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
        aidl = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module")
        }
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-process:2.6.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(project(mapOf("path" to ":config")))
    implementation(project(mapOf("path" to ":config")))
    implementation(project(mapOf("path" to ":config")))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")

    implementation("androidx.webkit:webkit:1.7.0")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("androidx.compose.animation:animation:1.4.3")
    implementation("androidx.navigation:navigation-compose:2.6.0")
//    implementation("com.google.android.enterprise.connectedapps:connectedapps:1.1.2")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    implementation("androidx.browser:browser:1.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.31.3-beta")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.gitlab.grrfe.httpkt:core:13.0.0-alpha.54")
    implementation("com.gitlab.grrfe.httpkt:ext-gson:13.0.0-alpha.54")
    implementation("com.gitlab.grrfe:gson-ext:8.1.2")
    implementation("com.gitlab.grrfe:kotlin-ext:0.0.23")
    implementation("com.github.1fexd:clearurlkt:0.0.23")
    implementation("com.github.1fexd:fastforwardkt:0.0.18")
    implementation("com.github.1fexd:libredirectkt:0.0.18")
    implementation("com.github.1fexd:mimetypekt:0.0.6")
    implementation("com.github.1fexd:amp2htmlkt:0.0.4")
    implementation("com.github.1fexd:stringbuilder-util-kt:1.0.1")
    implementation("com.github.1fexd:cached-urls:0.0.4")
    implementation("com.github.1fexd.android-pref-helper:preference-helper:0.0.13")
    implementation("com.github.1fexd.android-pref-helper:preference-helper-compose:0.0.13")
    implementation("com.github.1fexd:compose-route-util:0.0.12")
    implementation("com.github.1fexd:compose-dialog-helper:0.0.1")
    implementation("com.gitlab.grrfe:process-launcher:0.0.1")

    implementation("me.omico.lux:lux-androidx-compose-material3-pullrefresh")

    implementation("org.jsoup:jsoup:1.16.1")

    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")

    implementation("dev.rikka.shizuku:api:13.1.4")
    implementation("dev.rikka.shizuku:provider:13.1.4")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("dev.rikka.tools.refine:runtime:4.3.0")
    compileOnly("dev.rikka.hidden:stub:4.2.0")

    implementation(project(":interconnect"))
    implementation(project(":config"))

    testImplementation("io.insert-koin:koin-test:3.4.1")
    testImplementation("io.insert-koin:koin-android-test:3.4.0")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")

    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.3")
}