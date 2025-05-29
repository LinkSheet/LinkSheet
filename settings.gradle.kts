@file:Suppress("UnstableApiUsage")

import fe.build.dependencies.Grrfe
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
        maven { url = uri("https://storage.googleapis.com/r8-releases/raw") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
        id("com.android.library")
        id("org.jetbrains.kotlin.android")
        id("net.nemerosa.versioning")
        id("androidx.navigation.safeargs") version "2.8.2"
    }

    buildscript {
        dependencies {
            classpath("com.android.tools:r8:8.10.6-dev")
        }
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
    GradlePluginPortalRepository,
    MavenRepository("https://storage.googleapis.com/r8-releases/raw"),
    mode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
)

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":app", ":config")
includeProject(":test-instrument", "test-lib/instrument")
includeProject(":test-core", "test-lib/core")
includeProject(":scaffold", "lib/scaffold")
includeProject(":bottom-sheet", "lib/bottom-sheet")
includeProject(":bottom-sheet-new", "lib/bottom-sheet-new")
includeProject(":hidden-api", "lib/hidden-api")

buildSettings {
    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
        trySubstitute(Grrfe.httpkt, properties["httpkt.dir"])
        trySubstitute(Grrfe.gsonExt, properties["gson-ext.dir"])
        trySubstitute(_1fexd.composeKit, properties["composekit.dir"])
    }
}

