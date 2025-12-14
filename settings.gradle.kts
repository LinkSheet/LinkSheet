@file:Suppress("UnstableApiUsage")

import fe.build.dependencies.Grrfe
import fe.build.dependencies.LinkSheet
import fe.build.dependencies._1fexd
import fe.buildsettings.config.GradlePluginPortalRepository
import fe.buildsettings.config.MavenRepository
import fe.buildsettings.config.configureRepositories
import fe.buildsettings.extension.includeProject

rootProject.name = "LinkSheet"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.6"
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("com.android.library")
        id("org.jetbrains.kotlin.android")
        id("net.nemerosa.versioning")
        id("androidx.navigation.safeargs") version "2.8.2"
    }

    when (val gradleBuildDir = extra.properties["gradle.build.dir"]) {
        null -> {
            val gradleBuildVersion = extra.properties["gradle.build.version"]
            val plugins = extra.properties["gradle.build.plugins"]
                .toString().trim().split(",")
                .map { it.trim().split("=") }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }
            resolutionStrategy {
                eachPlugin {
                    plugins[requested.id.id]?.let { useModule("$it:$gradleBuildVersion") }
                }
            }
        }

        else -> includeBuild(gradleBuildDir.toString())
    }
}

plugins {
    id("de.fayard.refreshVersions")
    id("org.gradle.toolchains.foojay-resolver-convention")
    id("com.gitlab.grrfe.build-settings-plugin")
}

configureRepositories(
    MavenRepository.Google,
    MavenRepository.MavenCentral,
    MavenRepository.Jitpack,
    MavenRepository.Mozilla,
    MavenRepository("https://oss.sonatype.org/content/repositories/snapshots"),
    GradlePluginPortalRepository,
    MavenRepository("https://storage.googleapis.com/r8-releases/raw"),
    mode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
)

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":app", ":config")
includeProject(":test-instrument", "test-lib/instrument")
includeProject(":test-core", "test-lib/core")
includeProject(":test-fake", "test-lib/fake")
includeProject(":test-koin", "test-lib/koin")
includeProject(":scaffold", "lib/scaffold")
includeProject(":bottom-sheet", "lib/bottom-sheet")
includeProject(":bottom-sheet-new", "lib/bottom-sheet-new")
includeProject(":hidden-api", "lib/hidden-api")
includeProject(":util", "lib/util")
includeProject(":api", "lib/api")
includeProject(":log", "lib/log")
includeProject(":common", "lib/common")
includeProject(":compose", "lib/compose")
includeProject(":integration-embed-resolve", "integration/embed-resolve")
includeProject(":integration-clearurl", "integration/clearurl")
includeProject(":integration-amp2html", "integration/amp2html")
includeProject(":feature-app", "features/app")
includeProject(":feature-browser", "features/browser")
includeProject(":feature-engine", "features/engine")
includeProject(":feature-downloader", "features/downloader")
includeProject(":feature-libredirect", "features/libredirect")
includeProject(":feature-shizuku", "features/shizuku")
includeProject(":feature-systeminfo", "features/systeminfo")
includeProject(":feature-profile", "features/profile")
includeProject(":feature-wiki", "features/wiki")
includeProject(":sdk-rule-plugin", "sdk/rule-plugin")
includeProject(":sdk-common", "sdk/common")

buildSettings {
    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
        trySubstitute(Grrfe.httpkt, properties["httpkt.dir"])
        trySubstitute(Grrfe.gsonExt, properties["gson-ext.dir"])
        trySubstitute(_1fexd.composeKit, properties["composekit.dir"])
        trySubstitute(LinkSheet.flavors, properties["flavors.dir"])
    }
}

