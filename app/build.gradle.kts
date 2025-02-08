import fe.buildlogic.Version
import fe.buildlogic.dependency.Grrfe
import fe.buildlogic.dependency.LinkSheet
import fe.buildlogic.dependency.MozillaComponents
import fe.buildlogic.dependency.PinnedVersions
import fe.buildlogic.dependency._1fexd
import fe.buildlogic.extension.*
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
    id("build-logic-plugin")
    id("dev.rikka.tools.refine")
}

// Must be defined before the android block, or else it won't work
versioning {
    releaseMode = CurrentTagMode.closure
    releaseParser = TagReleaseParser.closure
}

var appName = "LinkSheet"
val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss")

android {
    namespace = "fe.linksheet"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = Version.MIN_SDK
        targetSdk = Version.COMPILE_SDK

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

//    val p = this@android.packaging
//    val r = p.resources
//    packaging {
//        resources {
////            it.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}", "META-INF/atomicfu.kotlin_module")
//        }
//    }

    val main by sourceSets
    for (it in arrayOf("compat", "experiment", "testing")) {
        main.java.srcDir("src/main/$it")
    }
}

dependencies {
    compileOnly(project(":hidden-api"))
    implementation(project(":config"))
//    implementation(project(":components"))
//    implementation(project(":compose-util"))
    implementation(project(":bottom-sheet"))
    implementation(project(":scaffold"))

    implementation(platform(Square.okHttp3.bom.withVersion("5.0.0-alpha.14")))
    //noinspection UseTomlInstead
    implementation(Square.okHttp3.android)
    //noinspection UseTomlInstead
    implementation(Square.okHttp3.coroutines)
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    implementation(platform(AndroidX.compose.bom))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui.text)
    implementation(AndroidX.compose.ui.withVersion(PinnedVersions.ComposeVersion))
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(PinnedVersions.Material3)

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

    implementation(COIL)
    implementation(COIL.compose)
    implementation("io.coil-kt.coil3:coil-network-ktor3:_")

    implementation("com.github.seancfoley:ipaddress:_")
    implementation("io.github.fornewid:placeholder-material3:_")
    implementation("io.viascom.nanoid:nanoid:_")

    implementation(LinkSheet.flavors)
    implementation(LinkSheet.interconnect)

    implementation(platform("com.github.1fexd:super:_"))

    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.mock)

    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.core)
    implementation(Grrfe.std.time.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.process.core)
    implementation(Grrfe.std.process.android)
    implementation(Grrfe.std.result.core)
    implementation(Grrfe.std.uri)

    implementation(platform(Grrfe.httpkt.bom))
    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.gson)
    implementation(Grrfe.ext.gson)

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

    implementation(platform(_1fexd.composeKit.bom))
    implementation(_1fexd.composeKit.app.core)
    implementation(_1fexd.composeKit.theme.core)
    implementation(_1fexd.composeKit.theme.preference)
    implementation(_1fexd.composeKit.component)
    implementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.layout)

    implementation("com.github.jeziellago:compose-markdown:_")

    implementation("app.cash.zipline:zipline-android:_")
    implementation("app.cash.zipline:zipline-loader-android:_")

    implementation("me.saket.unfurl:unfurl:_")
    implementation("me.saket.unfurl:unfurl-social:_")
//    implementation(libs.unfurl.social)

    "proImplementation"(platform("io.github.jan-tennert.supabase:bom:_"))
    "proImplementation"("io.github.jan-tennert.supabase:storage-kt:_")
    "proImplementation"("io.github.jan-tennert.supabase:compose-auth-ui:_")
    "proImplementation"("io.github.jan-tennert.supabase:gotrue-kt:_")
    "proImplementation"(Ktor.client.android)

    implementation("org.jsoup:jsoup:_")

    implementation("dev.rikka.shizuku:api:_")
    implementation("dev.rikka.shizuku:provider:_")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:_")
    implementation("dev.rikka.tools.refine:runtime:_")
//    compileOnly("dev.rikka.hidden:stub:_")

    implementation(MozillaComponents.support.utils)
    implementation(MozillaComponents.lib.publicSuffixList)
    implementation(KotlinX.serialization.json)


    androidTestImplementation(platform(AndroidX.compose.bom))
    androidTestImplementation(AndroidX.test.core)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.ext.junit.ktx)
    androidTestImplementation(AndroidX.room.testing)
    androidTestImplementation(AndroidX.compose.ui.testJunit4)
    androidTestImplementation("com.willowtreeapps.assertk:assertk:_")
    androidTestImplementation(kotlin("test"))

    debugImplementation(AndroidX.compose.ui.testManifest)

    testImplementation(Grrfe.std.result.assert)
    testImplementation(Koin.test)
    testImplementation(Koin.junit4)
    testImplementation(Koin.android)
    testImplementation(Testing.junit4)
    testImplementation(Testing.robolectric)
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation("com.github.gmazzo.okhttp.mock:mock-client:_")
    testImplementation(kotlin("test"))


    debugImplementation(Square.leakCanary.android)
    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
