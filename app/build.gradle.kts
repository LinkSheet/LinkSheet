import com.android.build.api.dsl.VariantDimension
import de.fayard.refreshVersions.core.versionFor
import fe.buildsrc.KotlinClosure4
import fe.buildsrc.Version
import fe.buildsrc.dependency.Grrfe
import fe.buildsrc.dependency.LinkSheet
import fe.buildsrc.dependency.MozillaComponents
import fe.buildsrc.dependency._1fexd
import fe.buildsrc.extension.getOrSystemEnv
import fe.buildsrc.extension.getOrSystemEnvOrDef
import fe.buildsrc.extension.readPropertiesOrNull
import net.nemerosa.versioning.ReleaseInfo
import net.nemerosa.versioning.SCMInfo
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
    releaseMode = KotlinClosure4<String?, String?, String?, VersioningExtension, String>({ _, _, currentTag, _ ->
        currentTag
    })

    releaseParser = KotlinClosure2<SCMInfo, String, ReleaseInfo>({ info, _ -> ReleaseInfo("release", info.tag) })
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
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = Version.MIN_SDK
        targetSdk = Version.COMPILE_SDK

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


        val localProperties = rootProject.file("local.properties").readPropertiesOrNull()
        val publicLocalProperties = rootProject.file("public.local.properties").readPropertiesOrNull()

        val supportedLocales = publicLocalProperties.getOrSystemEnv("SUPPORTED_LOCALES")?.split(",") ?: emptyList()
        resourceConfigurations.addAll(supportedLocales)

        buildConfigField("String[]", "SUPPORTED_LOCALES", buildString {
            append("{").append(supportedLocales.joinToString(",") { encodeString(it) }).append("}")
        })

        buildConfigField("int", "DONATION_BANNER_MIN", localProperties.getOrSystemEnvOrDef("DONATION_BANNER_MIN", "20"))

        arrayOf("LINK_DISCORD", "LINK_BUY_ME_A_COFFEE", "LINK_CRYPTO").forEach {
            buildStringConfigField(it, publicLocalProperties.getOrSystemEnv(it))
        }

        buildConfigField("long", "BUILT_AT", "$now")
        buildStringConfigField("COMMIT", versionInfo.commit)
        buildStringConfigField("BRANCH", versionInfo.branch)
        buildStringConfigField("GITHUB_WORKFLOW_RUN_ID", System.getenv("GITHUB_WORKFLOW_RUN_ID"))
        buildStringConfigField("APTABASE_API_KEY", localProperties.getOrSystemEnv("APTABASE_API_KEY"))
        buildConfigField(
            "boolean",
            "ANALYTICS_SUPPORTED",
            localProperties.getOrSystemEnvOrDef("ANALYTICS_SUPPORTED", "true")
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.isIncludeAndroidResources = true

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        register("env") {
            val properties = rootProject.file(".ignored/keystore.properties").readPropertiesOrNull()

            storeFile = properties.getOrSystemEnv("KEYSTORE_FILE_PATH")?.let { rootProject.file(it) }
            storePassword = properties.getOrSystemEnv("KEYSTORE_PASSWORD")
            keyAlias = properties.getOrSystemEnv("KEY_ALIAS")
            keyPassword = properties.getOrSystemEnv("KEY_PASSWORD")
        }
    }

    flavorDimensions += listOf("type")

    productFlavors {
        register("foss") {
            dimension = "type"
            buildStringConfigField("FLAVOR", "Foss")
        }

        register("pro") {
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

            isDebuggable = true

            resValue("string", "app_name", "$appName Release Debug")
        }

        register("migrate") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks.add("release")
            signingConfig = signingConfigs.getByName("env")

            resValue("string", "app_name", "$appName Migrate")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(Version.JVM)
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

    val main by sourceSets
    for (it in setOf("compat", "experiment")) {
        main.java.srcDir("src/main/$it")
    }
}

dependencies {
    implementation(project(":config"))
//    implementation(project(":components"))
//    implementation(project(":compose-util"))
    implementation(project(":bottom-sheet"))

    implementation(platform(Square.okHttp3.bom.withVersion("5.0.0-alpha.14")))
    //noinspection UseTomlInstead
    implementation(Square.okHttp3.android)
    //noinspection UseTomlInstead
    implementation(Square.okHttp3.coroutines)

    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.ui.withVersion("1.7.0"))
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3.withVersion("1.3.0"))

    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.activity.compose)
    implementation(libs.reorderable)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.compose.animation)
    implementation(AndroidX.navigation.compose)

    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.lifecycle.runtime.compose)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.startup.runtime)

    implementation(AndroidX.webkit)
    implementation(AndroidX.browser)

    implementation(AndroidX.room.runtime)
    implementation(AndroidX.room.ktx)
    ksp(AndroidX.room.compiler)

    implementation(Google.android.material)
    implementation(Google.accompanist.permissions)

    implementation(libs.junit.ktx)

    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(libs.kotlin.reflect)

    implementation(COIL)
    implementation(COIL.compose)


    implementation(libs.ipaddress)
    implementation(libs.placeholder.material3)
    implementation(libs.nanoid)

    implementation(LinkSheet.flavors)
    implementation(LinkSheet.interconnect)

    implementation(platform("com.github.1fexd:super:_"))

    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.gson)
    implementation(Grrfe.ext.gson)
    implementation(Grrfe.ext.kotlin)
    implementation(Grrfe.processLauncher)
    implementation(_1fexd.uriParser)
    implementation(_1fexd.clearUrl)
    implementation(_1fexd.signify)
    implementation(_1fexd.fastForward)
    implementation(_1fexd.libredirectkt)
    implementation(_1fexd.amp2html)
    implementation(_1fexd.stringBuilder)
    implementation(_1fexd.embedResolve)
    implementation(_1fexd.android.preference.core)
    implementation(_1fexd.android.preference.compose)
    implementation(_1fexd.android.preference.composeMock)
    implementation(_1fexd.android.compose.dialog)
    implementation(_1fexd.android.compose.route)
    implementation(_1fexd.android.span.compose)
    implementation(_1fexd.android.lifecycleUtil.core)
    implementation(_1fexd.android.lifecycleUtil.koin)
    implementation(_1fexd.composeKit.app.core)
    implementation(_1fexd.composeKit.theme.core)
    implementation(_1fexd.composeKit.theme.preference)
    implementation(_1fexd.composeKit.component)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.layout)

    implementation(libs.zipline.android)
    implementation(libs.zipline.loader.android)

    implementation(libs.unfurl)
//    implementation(libs.unfurl.social)

    "proImplementation"(platform(libs.bom))
    "proImplementation"(libs.storage.kt)
    "proImplementation"(libs.compose.auth.ui)
    "proImplementation"(libs.gotrue.kt)
    "proImplementation"(Ktor.client.android)

    implementation(libs.jsoup)

    implementation(libs.api)
    implementation(libs.provider)
    implementation(libs.hiddenapibypass)
    implementation(libs.dev.rikka.tools.refine.runtime)
    compileOnly(libs.stub)

    implementation(MozillaComponents.support.utils)
    implementation(MozillaComponents.lib.publicSuffixList)

    testImplementation(Koin.test)
    testImplementation(libs.koin.android.test)

    debugImplementation(Square.leakCanary.android)

    testImplementation(Testing.robolectric)
    testImplementation(Testing.junit.jupiter)

    testImplementation(AndroidX.test.core)
    testImplementation(AndroidX.test.coreKtx)
    testImplementation(AndroidX.test.ext.truth)
    testImplementation(AndroidX.test.runner)
    androidTestUtil(AndroidX.test.orchestrator)
    androidTestImplementation(platform(AndroidX.compose.bom))

    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
