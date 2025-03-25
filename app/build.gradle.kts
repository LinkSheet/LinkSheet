import fe.build.dependencies.Grrfe
import fe.build.dependencies.LinkSheet
import fe.build.dependencies.MozillaComponents
import fe.build.dependencies._1fexd
import fe.buildlogic.Version
import fe.buildlogic.extension.*
import fe.buildlogic.version.AndroidVersionStrategy
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("net.nemerosa.versioning")
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("dev.rikka.tools.refine")
    id("com.gitlab.grrfe.build-logic-plugin")
}

// Must be defined before the android block, or else it won't work
versioning {
    releaseMode = CurrentTagMode.closure
    releaseParser = TagReleaseParser.closure
}

val appName = "LinkSheet"
val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")

android {
    namespace = "fe.linksheet"
    compileSdk = 35

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = Version.MIN_SDK
        targetSdk = 35

        val now = System.currentTimeMillis()
        val provider = AndroidVersionStrategy(now)

        val versionProvider = versioning.asProvider(project, provider)
        val (name, code, commit, branch) = versionProvider.get()

        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.of("UTC"))

        versionCode = code
        versionName = name

        setProperty("archivesBaseName", "$appName-${dtf.format(localDateTime)}-$versionName")

        val localProperties = rootProject.file("local.properties").readPropertiesOrNull()
        val publicLocalProperties = rootProject.file("public.local.properties").readPropertiesOrNull()

        val supportedLocales = publicLocalProperties.getOrSystemEnv("SUPPORTED_LOCALES")?.split(",") ?: emptyList()
        resourceConfigurations.addAll(supportedLocales)

        buildConfig {
            stringArray("SUPPORTED_LOCALES", supportedLocales)
            int("DONATION_BANNER_MIN", localProperties.getOrSystemEnv("DONATION_BANNER_MIN")?.toIntOrNull() ?: 20)

            arrayOf("LINK_DISCORD", "LINK_BUY_ME_A_COFFEE", "LINK_CRYPTO").forEach {
                string(it, publicLocalProperties.getOrSystemEnv(it))
            }

            long("BUILT_AT", now)
            string("COMMIT", commit)
            string("BRANCH", branch)
            string("GITHUB_WORKFLOW_RUN_ID", System.getenv("GITHUB_WORKFLOW_RUN_ID"))
            string("APTABASE_API_KEY", localProperties.getOrSystemEnv("APTABASE_API_KEY"))
            boolean(
                "ANALYTICS_SUPPORTED",
                localProperties.getOrSystemEnv("ANALYTICS_SUPPORTED")?.toBooleanStrictOrNull() != false
            )

            string("FLAVOR_CONFIG", System.getenv("FLAVOR_CONFIG"))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testOptions.unitTests.isIncludeAndroidResources = true

        vectorDrawables {
            useSupportLibrary = true
        }

        room {
            schemaDirectory("$projectDir/schemas")
            generateKotlin = true
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

        register("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(Version.JVM)
        compilerOptions.freeCompilerArgs.addAll(
            "-Xwhen-guards",
            "-P", "plugin:org.jetbrains.kotlin.parcelize:experimentalCodeGeneration=true"
        )
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    val androidTest by sourceSets
    androidTest.assets.srcDir("$projectDir/schemas")

    packaging {

//        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

//        resources {
//            it.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module")
//        }
    }

    val main by sourceSets
    for (it in arrayOf("compat", "experiment", "testing")) {
        main.java.srcDir("src/main/$it")
    }
}

dependencies {
    implementation("io.ktor:ktor-client-okhttp-jvm:3.1.1")
    compileOnly(project(":hidden-api"))
    implementation(project(":config"))

//    implementation(project(":components"))
//  dd
//    implementation(project(":compose-util"))
    implementation(project(":bottom-sheet"))
    implementation(project(":bottom-sheet-new"))
    implementation(project(":scaffold"))

//    implementation(platform(Square.okHttp3.bom))
    implementation(Square.okHttp3.android)
    implementation(Square.okHttp3.coroutines)
//    implementation(Square.okHttp3.mockWebServer3)
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui.withVersion("1.8.0-rc03"))
    implementation(AndroidX.compose.ui.text)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3.withVersion("1.4.0-alpha12"))

    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.activity.compose)
    implementation("sh.calvin.reorderable:reorderable:_")

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

    implementation(AndroidX.test.ext.junit.ktx)

    implementation(Koin.android)
    implementation(Koin.compose)
    implementation("org.jetbrains.kotlin:kotlin-reflect:_")

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-core:3.1.0")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-ktor3:3.1.0")

    implementation("com.github.seancfoley:ipaddress:_")
    implementation("io.github.fornewid:placeholder-material3:_")
    implementation("io.viascom.nanoid:nanoid:_")

    implementation(LinkSheet.flavors)
    implementation(LinkSheet.interconnect)

    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.mock)
    implementation(JetBrains.ktor.client.logging)

    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.result.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.std.stringbuilder)
    implementation(Grrfe.std.test)
    implementation(Grrfe.std.process.core)

    implementation(platform(Grrfe.httpkt.bom))
    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.serialization.gson)

    implementation(platform(Grrfe.gsonExt.bom))
    implementation(Grrfe.gsonExt.core)

    implementation(_1fexd.clearUrl)
    implementation(_1fexd.signify)
    implementation(_1fexd.fastForward)
    implementation(_1fexd.libredirectkt)
    implementation(_1fexd.amp2html)
    implementation(_1fexd.embedResolve)

    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.layout)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.app)
    implementation(_1fexd.composeKit.compose.theme.core)
    implementation(_1fexd.composeKit.compose.theme.preference)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.process)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.span.core)
    implementation(_1fexd.composeKit.span.compose)

    runtimeOnly(AndroidX.annotation)

    implementation("com.github.jeziellago:compose-markdown:_")

    implementation("app.cash.zipline:zipline-android:_")
    implementation("app.cash.zipline:zipline-loader-android:_")

    implementation("me.saket.unfurl:unfurl:_")
//    implementation(libs.unfurl.social)

//    "proImplementation"(platform("io.github.jan-tennert.supabase:bom:_"))
//    "proImplementation"("io.github.jan-tennert.supabase:storage-kt:_")
//    "proImplementation"("io.github.jan-tennert.supabase:compose-auth-ui:_")
//    "proImplementation"("io.github.jan-tennert.supabase:gotrue-kt:_")
//    "proImplementation"(Ktor.client.android)

    implementation("org.jsoup:jsoup:_")

    implementation("dev.rikka.shizuku:api:_")
    implementation("dev.rikka.shizuku:provider:_")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:_")
    implementation("dev.rikka.tools.refine:runtime:_")
//    compileOnly("dev.rikka.hidden:stub:_")

    implementation(MozillaComponents.support.utils)
    implementation(MozillaComponents.lib.publicSuffixList)
    implementation(KotlinX.serialization.json)

    val commonTestDependencies = arrayOf(
        Koin.test,
        Koin.junit4,
        Koin.android,
        KotlinX.coroutines.test,
        AndroidX.room.testing,
        Grrfe.std.test,
        Grrfe.std.result.assert,
        Testing.robolectric,
        "com.willowtreeapps.assertk:assertk:_",
        kotlin("test")
    )

    for (notation in commonTestDependencies) {
        androidTestImplementation(notation)
        testImplementation(notation)
    }

    testImplementation("org.mock-server:mockserver-client-java:5.15.0")
    implementation(platform("org.testcontainers:testcontainers-bom:1.20.6"))
    testImplementation("org.testcontainers:mockserver")
    testImplementation("org.testcontainers:toxiproxy")

    androidTestImplementation(AndroidX.test.core)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.ext.junit.ktx)
    androidTestImplementation(AndroidX.test.uiAutomator)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)

    testImplementation("com.github.gmazzo.okhttp.mock:mock-client:_")

    debugImplementation(Square.leakCanary.android)
    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
