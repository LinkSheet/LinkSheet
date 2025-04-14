import fe.build.dependencies.Grrfe
import fe.build.dependencies._1fexd
import fe.buildsettings.config.GradlePluginPortalRepository
import fe.buildsettings.config.MavenRepository
import fe.buildsettings.config.configureRepositories

rootProject.name = "LinkSheet"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
        id("net.nemerosa.versioning")
        id("com.android.library")
        id("org.jetbrains.kotlin.android")
        id("androidx.navigation.safeargs") version "2.8.2"
    }

    when (val gradleBuildDir = extra.properties["gradle.build.dir"]) {
        null -> {
            val gradleBuildVersion = extra.properties["gradle.build.version"]
            val plugins = mapOf(
                "com.gitlab.grrfe.build-settings-plugin" to "com.gitlab.grrfe.gradle-build:build-settings",
                "com.gitlab.grrfe.build-logic-plugin" to "com.gitlab.grrfe.gradle-build:build-logic"
            )

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
    id("com.gitlab.grrfe.build-settings-plugin")
}

configureRepositories(
    MavenRepository.Google,
    MavenRepository.MavenCentral,
    MavenRepository.Jitpack,
    MavenRepository.Mozilla,
    GradlePluginPortalRepository
)

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":app", ":config")
include(":bottom-sheet", ":bottom-sheet-new")
include(":scaffold")
include(":hidden-api")

buildSettings {
    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
        trySubstitute(Grrfe.httpkt, properties["httpkt.dir"])
        trySubstitute(Grrfe.gsonExt, properties["gson-ext.dir"])
        trySubstitute(_1fexd.composeKit, properties["composekit.dir"])
    }
}

