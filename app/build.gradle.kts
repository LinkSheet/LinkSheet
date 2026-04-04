
//import com.gitlab.grrfe.gradlebuild.common.version.asProvider
import com.gitlab.grrfe.gradlebuild.Version
import com.gitlab.grrfe.gradlebuild.android.AndroidSdk
import com.gitlab.grrfe.gradlebuild.android.ArchiveBaseName
import com.gitlab.grrfe.gradlebuild.android.extension.buildConfig
import com.gitlab.grrfe.gradlebuild.android.extension.buildStringConfigField
import com.gitlab.grrfe.gradlebuild.android.version.AndroidVersionStrategy
import com.gitlab.grrfe.gradlebuild.common.CompilerOption
import com.gitlab.grrfe.gradlebuild.common.KotlinCompilerArgs
import com.gitlab.grrfe.gradlebuild.common.PluginOption
import com.gitlab.grrfe.gradlebuild.util.PropertiesFile
import com.gitlab.grrfe.gradlebuild.util.SystemEnvironment
import com.gitlab.grrfe.gradlebuild.util.withProviders
import com.gitlab.grrfe.gradlebuild.version.CurrentTagMode
import com.gitlab.grrfe.gradlebuild.version.TagReleaseParser
import com.gitlab.grrfe.gradlebuild.version.asProvider
import com.gitlab.grrfe.gradlebuild.version.closure
import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.android.application")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("net.nemerosa.versioning")
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("dev.rikka.tools.refine")
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.mannodermaus.android-junit5")
//    id("io.github.gmazzo.gitversion")

}

// Must be defined before the android block, or else it won't work
versioning {
    releaseMode = CurrentTagMode.closure
    releaseParser = TagReleaseParser.closure
}

//
//gitVersion {
//    tagPrefix = "" // Optional, default is "v"
//    initialVersion = "0.1.0-SNAPSHOT" // Optional, default is "0.1.0-SNAPSHOT"
//}


val appName = "LinkSheet"

android {
    namespace = "fe.linksheet"
    compileSdk = AndroidSdk.COMPILE_SDK

    defaultConfig {
        applicationId = "fe.linksheet"
        minSdk = AndroidSdk.MIN_SDK
        targetSdk = AndroidSdk.COMPILE_SDK

        val now = System.currentTimeMillis()
        val provider = AndroidVersionStrategy(now)

//        val version = gitVersion.from(AndroidVersionStrategy2(now))
        val versionProvider = versioning.asProvider(project, provider)
        val (name, code, commit, branch) = versionProvider.get()

        versionCode = code
        versionName = name

        with(ArchiveBaseName) {
            project.base.setArchivesName(appName, name, now)
        }
        val localProperties = with(PropertiesFile) {
            rootProject.file("local.properties").readPropertiesOrNull()
        }
        val publicLocalProperties = with(PropertiesFile) {
            rootProject.file("public.local.properties").readPropertiesOrNull()
        }
        val localProviders = withProviders(localProperties, SystemEnvironment)
        val publicLocalProviders = withProviders(publicLocalProperties, SystemEnvironment)

        val supportedLocales = publicLocalProviders.get("SUPPORTED_LOCALES")?.split(",") ?: emptyList()
        androidResources {
            @Suppress("UnstableApiUsage")
            localeFilters += supportedLocales
        }
        tasks.register("createLocaleConfig") {
            val localeString = supportedLocales.joinToString(
                separator = System.lineSeparator(),
            ) { "\t<locale android:name=\"$it\" />" }

            val xml = """<?xml version="1.0" encoding="utf-8"?>
            |<locale-config xmlns:android="http://schemas.android.com/apk/res/android">
            |$localeString 
            |</locale-config>
            """.trimMargin()

            file("src/main/res/xml/locales_config.xml").writeText(xml)
        }

        buildConfig {
            stringArray("SUPPORTED_LOCALES", supportedLocales)
            int("DONATION_BANNER_MIN", localProviders.get("DONATION_BANNER_MIN")?.toIntOrNull() ?: 20)

            arrayOf("LINK_DISCORD", "LINK_BUY_ME_A_COFFEE", "LINK_CRYPTO").forEach {
                string(it, publicLocalProviders.get(it))
            }

            long("BUILT_AT", now)
            string("COMMIT", commit)
            string("BRANCH", branch)
            boolean("IS_CI", System.getenv("CI")?.toBooleanStrictOrNull() == true)
            string("GITHUB_WORKFLOW_RUN_ID", System.getenv("GITHUB_WORKFLOW_RUN_ID"))
            string("APTABASE_API_KEY", localProviders.get("APTABASE_API_KEY"))
            boolean(
                "ANALYTICS_SUPPORTED",
                localProviders.get("ANALYTICS_SUPPORTED")?.toBooleanStrictOrNull() != false
            )

            string("FLAVOR_CONFIG", System.getenv("FLAVOR_CONFIG"))
            string("API_HOST", localProviders.get("API_HOST"))
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
        testOptions.unitTests.isIncludeAndroidResources = true

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        register("env") {
            val properties = with(PropertiesFile) {
                rootProject.file(".ignored/keystore.properties").readPropertiesOrNull()
            }
            val provider = withProviders(properties, SystemEnvironment)
            storeFile = provider.get("KEYSTORE_FILE_PATH")?.let { rootProject.file(it) }
            storePassword = provider.get("KEYSTORE_PASSWORD")
            keyAlias = provider.get("KEY_ALIAS")
            keyPassword = provider.get("KEY_PASSWORD")
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
            isShrinkResources = true
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

    buildFeatures {
        aidl = true
        buildConfig = true
        resValues = true
    }

    packaging {
       resources.pickFirsts += "**/google/protobuf/**"
    }

    lint {
        disable += arrayOf(
            "AndroidGradlePluginVersion",
            "MissingTranslation",
            "Untranslatable"
        )
        baseline = file("lint-baseline.xml")
    }

    testOptions {
        unitTests.all { test ->
            test.testLogging {
                test.outputs.upToDateWhen { false }
                events("passed", "skipped", "failed", "standardOut", "standardError")
                showCauses = true
                showExceptions = true
            }
        }
    }

    val androidTest by sourceSets
    androidTest.assets.srcDir("$projectDir/schemas")
    rootProject.findProject(":feature-libredirect")?.projectDir?.resolve("schemas")?.let {
        androidTest.assets.srcDir(it)
    }

    val main by sourceSets
    for (it in arrayOf("compat", "experiment", "testing")) {
        main.kotlin.srcDir("src/main/$it")
    }
}

kotlin {
    jvmToolchain(Version.JVM)
//    addCompilerOptions()
//    addPluginOptions(PluginOption.Parcelize.ExperimentalCodeGeneration to true)
    with(compilerOptions.freeCompilerArgs) {
        addAll(KotlinCompilerArgs.createCompilerOptions(CompilerOption.SkipPreReleaseCheck))
        addAll(KotlinCompilerArgs.createPluginOptions(PluginOption.Parcelize.ExperimentalCodeGeneration to true))
    }
}

room {
    schemaDirectory("$projectDir/schemas")
    generateKotlin = true
}


junitPlatform {
    instrumentationTests {
        includeExtensions.set(true)
    }
}

dependencies {
    implementation(project(":feature-app"))
    implementation(project(":feature-browser"))
    implementation(project(":feature-devicecompat"))
    implementation(project(":feature-engine"))
    implementation(project(":feature-downloader"))
    implementation(project(":feature-libredirect"))
    implementation(project(":feature-shizuku"))
    implementation(project(":feature-systeminfo"))
    implementation(project(":feature-profile"))
    implementation(project(":feature-wiki"))
    implementation(project(":integration-clearurl"))
    implementation(project(":integration-embed-resolve"))
    implementation(project(":integration-amp2html"))

    compileOnly(project(":hidden-api"))
    implementation(project(":config"))
    implementation(project(":log"))
    implementation(project(":util"))
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":compose"))

    implementation(project(":bottom-sheet"))
    implementation(project(":bottom-sheet-new"))
    implementation(project(":scaffold"))
    implementation(project(":test-fake"))

    testImplementation(project(":test-core"))
    testImplementation(project(":test-koin"))
    androidTestImplementation(project(":test-instrument"))

//    implementation(platform(Square.okHttp3.bom))
    implementation(Square.okHttp3.android)
    implementation(Square.okHttp3.coroutines)
//    implementation(Square.okHttp3.mockWebServer3)
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)
    implementation(platform("androidx.compose:compose-bom-alpha:_"))
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.text)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.material3)

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
    implementation(AndroidX.work.runtimeKtx)
    testImplementation(AndroidX.work.testing)

    implementation(AndroidX.room.runtime)
    implementation(AndroidX.room.ktx)
    ksp(AndroidX.room.compiler)

    implementation(Google.android.material)
    implementation(Google.accompanist.permissions)

    implementation(Koin.android)
    implementation(Koin.compose)
    implementation(Koin.workManager)
    implementation("org.jetbrains.kotlin:kotlin-reflect:_")

    implementation("io.coil-kt.coil3:coil-compose:_")
    implementation("io.coil-kt.coil3:coil-core:_")
    implementation("io.coil-kt.coil3:coil-compose:_")
    implementation("io.coil-kt.coil3:coil-network-okhttp:_")
    implementation("io.coil-kt.coil3:coil-network-okhttp:_")
    implementation("io.coil-kt.coil3:coil-network-ktor3:_")
    implementation("io.coil-kt.coil3:coil-svg:_")
    implementation("io.coil-kt.coil3:coil-test:_")

    implementation("com.github.seancfoley:ipaddress:_")
    implementation("io.github.fornewid:placeholder-material3:_")
    implementation("io.viascom.nanoid:nanoid:_")

//    implementation(LinkSheet.flavors.core)
//    implementation(LinkSheet.flavors.interconnect.core)
    implementation("com.github.LinkSheet.flavors:interconnect-core:_")
    implementation("com.github.LinkSheet.flavors:core:_")

    implementation(JetBrains.ktor.client.core)
    implementation(JetBrains.ktor.client.gson)
    implementation(JetBrains.ktor.client.okHttp)
    implementation(JetBrains.ktor.client.android)
    implementation(JetBrains.ktor.client.logging)
    implementation(JetBrains.ktor.client.contentNegotiation)
    implementation(JetBrains.ktor.client.json)
    implementation(JetBrains.ktor.client.encoding)
    implementation(JetBrains.ktor.plugins.serialization.gson)
    implementation("io.ktor:ktor-client-okhttp-jvm:_")
    testImplementation(JetBrains.ktor.client.mock)

    implementation(Grrfe.std.core)
    implementation(Grrfe.std.coroutines)
    implementation(Grrfe.std.time.core)
    implementation(Grrfe.std.time.java)
    implementation(Grrfe.std.result.core)
    implementation(Grrfe.std.uri)
    implementation(Grrfe.std.stringbuilder)
    implementation(Grrfe.std.test)
    implementation(Grrfe.std.process.core)


    implementation(Grrfe.httpkt.core)
    implementation(Grrfe.httpkt.serialization.gson)

    implementation(Grrfe.gsonExt.core)
    implementation(Grrfe.gsonExt.koin)

    implementation(Grrfe.signify)
    implementation(_1fexd.fastForward)
    implementation("com.github.1fexd.libredirectkt:lib:_")

    implementation(_1fexd.composeKit.compose.core)
    implementation(_1fexd.composeKit.compose.layout)
    implementation(_1fexd.composeKit.compose.component)
    implementation(_1fexd.composeKit.compose.app)
    implementation(_1fexd.composeKit.compose.theme.core)
    implementation(_1fexd.composeKit.compose.theme.preference)
    implementation(_1fexd.composeKit.compose.dialog)
    implementation(_1fexd.composeKit.compose.route)
    implementation(_1fexd.composeKit.core)
    testImplementation(_1fexd.composeKit.core)
    implementation(_1fexd.composeKit.koin)
    implementation(_1fexd.composeKit.process)
    implementation(_1fexd.composeKit.lifecycle.compose)
    implementation(_1fexd.composeKit.lifecycle.core)
    implementation(_1fexd.composeKit.lifecycle.koin)
    implementation(_1fexd.composeKit.lifecycle.network.core)
    implementation(_1fexd.composeKit.lifecycle.network.koin)
    implementation(_1fexd.composeKit.preference.core)
    implementation(_1fexd.composeKit.preference.compose.core)
    implementation(_1fexd.composeKit.preference.compose.core2)
    implementation(_1fexd.composeKit.preference.compose.mock)
    implementation(_1fexd.composeKit.preference.compose.mock2)
    implementation(_1fexd.composeKit.span.core)
    implementation(_1fexd.composeKit.span.compose)

    runtimeOnly(AndroidX.annotation)

    implementation("app.cash.zipline:zipline-android:_")
    implementation("app.cash.zipline:zipline-loader-android:_")

    implementation("me.saket.unfurl:unfurl:_")
    implementation("com.github.nanihadesuka:LazyColumnScrollbar:_")

    implementation("org.jsoup:jsoup:_")

    implementation("dev.rikka.shizuku:api:_")
    implementation("dev.rikka.shizuku:provider:_")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:_")
    implementation("dev.rikka.tools.refine:runtime:_")

    implementation(KotlinX.serialization.json)
    implementation(KotlinX.serialization.protobuf)
    implementation(KotlinX.serialization.cbor)
    implementation("com.akuleshov7:ktoml-core:0.7.1")
    implementation("com.akuleshov7:ktoml-file:0.7.1")
    implementation("com.akuleshov7:ktoml-source:0.7.1")

    val commonTestDependencies = arrayOf(
        Koin.test,
        Koin.junit4,
        Koin.android,
        KotlinX.coroutines.test,
        Grrfe.std.test,
        Grrfe.std.result.assert,
        Testing.robolectric,
        CashApp.turbine,
        AndroidX.room.testing,
        AndroidX.test.ext.junit.ktx,
        AndroidX.compose.ui.test,
        AndroidX.compose.ui.testJunit4,
        "com.willowtreeapps.assertk:assertk:_",
        AndroidX.test.espresso.core,
    )

    for (notation in commonTestDependencies) {
        androidTestImplementation(notation)
        testImplementation(notation)
    }

    testImplementation("org.mock-server:mockserver-client-java:_")
    testImplementation("org.testcontainers:mockserver:_")
    testImplementation("org.testcontainers:toxiproxy:_")

    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:_")
    testImplementation(Testing.junit4)

    testImplementation(Testing.junit.jupiter.params)

    androidTestImplementation(Testing.junit.jupiter.api)
    androidTestImplementation(AndroidX.test.uiAutomator)
    androidTestImplementation(AndroidX.test.coreKtx)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(Testing.junit.jupiter.params)
    androidTestImplementation("de.mannodermaus.junit5:android-test-compose:_")
    testImplementation("com.github.gmazzo.okhttp.mock:mock-client:_")
    debugImplementation(Square.leakCanary.android)
    debugImplementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.testManifest)
}
