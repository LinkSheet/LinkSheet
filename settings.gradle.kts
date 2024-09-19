@file:Suppress("UnstableApiUsage")

import java.util.*

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
    }
}

plugins {
    id("de.fayard.refreshVersions")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.mozilla.org/maven2") }
        mavenLocal()
    }
}

rootProject.name = "LinkSheet"

include(":app", ":config")
include(":bottom-sheet")
include(":benchmark")

val isCI = System.getenv("CI")?.toBooleanStrictOrNull() == true
val dev = false

if (!isCI && dev) {
    val properties = Properties().apply {
        file("local.properties").reader().use { load(it) }
    }

    properties["android-lifecycle-util.dir"]?.let { lifecycleUtilDir ->
        includeBuild(lifecycleUtilDir) {
            val projects = setOf("core", "koin")

            dependencySubstitution {
                for (project in projects) {
                    substitute(module("com.github.1fexd.android-lifecycle-util:$project")).using(project(":$project"))
                }
            }
        }
    }

    properties["composekit.dir"]?.let { composeKitDir ->
        includeBuild(composeKitDir) {
            val projects = setOf("app-core", "theme-core", "theme-preference", "component", "core", "layout")

            dependencySubstitution {
                for (project in projects) {
                    substitute(module("com.github.1fexd.composekit:$project")).using(project(":$project"))
                }
            }
        }
    }
}

