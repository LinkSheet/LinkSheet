import com.android.build.api.dsl.VariantDimension
import de.fayard.refreshVersions.core.versionFor
import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.SCMInfo
import groovy.lang.Closure
import net.nemerosa.versioning.VersioningExtension
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    releaseMode =
        KotlinClosure4<String?, String?, String?, VersioningExtension, String>({ _, _, currentTag, _ ->
            currentTag
        })

    releaseParser = KotlinClosure2<SCMInfo, String, ReleaseInfo>({ info, _ ->
        ReleaseInfo("release", info.tag)
    })
}

var appName = "LinkSheet"
val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")

fun VariantDimension.buildStringConfigField(name: String, value: String? = null) {
    buildConfigField("String", name, encodeString(value))
}

fun encodeString(value: String? = null): String {
    return if (value == null) "null" else "\"${value}\""
}

android {
    namespace = "fe.linksheet"
    compileSdk = 34

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = 25
        targetSdk = 34

        val now = System.currentTimeMillis()
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"))
        val versionInfo = providers.provider { versioning.info }.get()

        versionCode = versionInfo.tag?.let {
            versionInfo.versionNumber.versionCode
        } ?: (now / 1000).toInt()

        versionName = versionInfo.tag ?: versionInfo.full
        val archivesBaseName = if (versionInfo.tag != null) {
            "$appName-$versionName"
        } else "$appName-${dtf.format(localDateTime)}-$versionName"

        setProperty("archivesBaseName", archivesBaseName)

        val supportedLocales = arrayOf(
            "en", "es", "ar", "bg", "bn", "de", "it", "pl", "ru", "tr", "zh", "zh-rTW"
        )

        resourceConfigurations.addAll(supportedLocales)

        buildConfigField("String[]", "SUPPORTED_LOCALES", buildString {
            append("{").append(supportedLocales.joinToString(",") { encodeString(it) }).append("}")
        })

        buildConfigField("long", "BUILT_AT", "$now")
        buildStringConfigField("COMMIT", versionInfo.commit)
        buildStringConfigField("BRANCH", versionInfo.branch)
        buildStringConfigField("GITHUB_WORKFLOW_RUN_ID", System.getenv("GITHUB_WORKFLOW_RUN_ID"))

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

    productFlavors {
        create("foss") {
            dimension = "type"
            buildStringConfigField("FLAVOR", "Foss")
        }

        create("pro") {
            dimension = "type"

            applicationIdSuffix = ".pro"
            versionNameSuffix = "-pro"
            buildStringConfigField("FLAVOR", "Pro")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            resValue("string", "app_name", "$appName Debug")
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        register("nightly") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("env")

            applicationIdSuffix = ".nightly"
            versionNameSuffix = "-nightly"

            resValue("string", "app_name", "$appName Nightly")
        }

        register("releaseDebug") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = buildTypes.getByName("debug").signingConfig

            applicationIdSuffix = ".release_debug"
            versionNameSuffix = "-release_debug"
            resValue("string", "app_name", "$appName Release Debug")
        }
    }

    flavorDimensions += listOf("type")



    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    buildFeatures {
        compose = true
        aidl = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.compiler)
    }

    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module")
        }
    }
}

dependencies {
    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.ui.graphics)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.animation)
    implementation(AndroidX.navigation.compose)

    implementation(libs.linkSheetInterConnect)
    implementation(project(":config"))
    implementation(libs.uriparser)

    implementation(AndroidX.lifecycle.process)
    androidTestImplementation(platform(AndroidX.compose.bom))
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(libs.kotlin.reflect)

    implementation(AndroidX.room.runtime)
    implementation(AndroidX.room.ktx)
    ksp(AndroidX.room.compiler)

    implementation(AndroidX.webkit)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.activity.compose)


//    implementation("com.google.android.enterprise.connectedapps:connectedapps:_")

    implementation(AndroidX.lifecycle.runtime.compose)

    implementation(AndroidX.browser)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(libs.gson)

    implementation(libs.com.gitlab.grrfe.httpkt.core)
    implementation(libs.ext.gson)
    implementation(libs.gson.ext)
    implementation(libs.kotlin.ext)
    implementation(libs.clearurlkt)
    implementation(libs.fastforwardkt)
    implementation(libs.libredirectkt)
    implementation(libs.mimetypekt)
    implementation(libs.amp2htmlkt)
    implementation(libs.stringbuilder.util.kt)
    implementation(libs.cached.urls)
    implementation(libs.preference.helper)
    implementation(libs.preference.helper.compose)
    implementation(libs.compose.route.util)
    implementation(libs.compose.dialog.helper)
    implementation(libs.process.launcher)

    implementation(libs.compose.material3.pullrefresh)
    implementation(libs.jsoup)

    implementation(Google.android.material)
    // Deprecated in favor of Activity.enableEdgeToEdge from androidx.activity 1.8+
    //FIXME: See the example PR in the migration guide here:
    // https://google.github.io/accompanist/systemuicontroller/
    implementation(libs.accompanist.systemuicontroller)
    implementation(Google.accompanist.permissions)

    implementation(libs.api)
    implementation(libs.provider)
    implementation(libs.hiddenapibypass)
    implementation(libs.dev.rikka.tools.refine.runtime)
    compileOnly(libs.stub)



    testImplementation(Koin.test)
    testImplementation(libs.koin.android.test)
    testImplementation(Testing.junit4)

    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)

    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}