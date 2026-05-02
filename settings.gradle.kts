@file:Suppress("UnstableApiUsage")

import com.gitlab.grrfe.gradlebuild.config.GradlePluginPortalRepository
import com.gitlab.grrfe.gradlebuild.config.MavenRepository
import com.gitlab.grrfe.gradlebuild.config.configureRepositories
import com.gitlab.grrfe.gradlebuild.extension.includeProject
import fe.build.dependencies.Grrfe
import fe.build.dependencies.LinkSheet
import fe.build.dependencies._1fexd

rootProject.name = "LinkSheet"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroupAndSubgroups("com.gitlab.grrfe")
                includeGroupAndSubgroups("com.github.1fexd")
            }
        }
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.6"
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("com.android.library")
        id("androidx.navigation.safeargs") version "2.9.6"
    }

    when (val gradleBuildDir = extra.properties["gradle.build.dir"]) {
        null -> {
            val gradleBuildVersion = extra.properties["gradle.build.version"]
            resolutionStrategy {
                eachPlugin {
                    with(requested.id) {
                        if (namespace == "com.gitlab.grrfe") {
                            useModule("com.gitlab.grrfe.gradle-build:$name:$gradleBuildVersion")
                        }
                    }
                }
            }
        }
        else -> includeBuild(gradleBuildDir.toString())
    }
}

plugins {
    id("de.fayard.refreshVersions")
    id("org.gradle.toolchains.foojay-resolver-convention")
    id("com.gitlab.grrfe.settings-build-plugin")
}

configureRepositories(
    MavenRepository.Google,
    MavenRepository.MavenCentral,
    MavenRepository.Jitpack,
    MavenRepository.Mozilla,
    MavenRepository("https://oss.sonatype.org/content/repositories/snapshots"),
    GradlePluginPortalRepository,
//    MavenRepository.local(),
    mode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
)

extra.properties["gradle.build.dir"]
    ?.let { includeBuild(it.toString()) }

include(":app", ":config")


includeProject(":sdk-rule-plugin", "sdk/rule-plugin")
includeProject(":sdk-common", "sdk/common")

buildSettings {
    projects("features") {
        includeProject(":feature-app", "app")
        includeProject(":feature-browser", "browser")
        includeProject(":feature-engine", "engine")
        includeProject(":feature-devicecompat", "devicecompat")
        includeProject(":feature-downloader", "downloader")
        includeProject(":feature-libredirect", "libredirect")
        includeProject(":feature-shizuku", "shizuku")
        includeProject(":feature-systeminfo", "systeminfo")
        includeProject(":feature-profile", "profile")
        includeProject(":feature-wiki", "wiki")
    }
    projects("integration") {
        includeProject(":integration-amp2html", "amp2html")
        includeProject(":integration-mime-types", "mime-types")
        includeProject(":integration-clearurl", "clearurl")
        includeProject(":integration-embed-resolve", "embed-resolve")
    }
    projects("lib") {
        includeProject(":lib-scaffold", "scaffold")
        includeProject(":lib-bottom-sheet", "bottom-sheet")
        includeProject(":lib-bottom-sheet-new", "bottom-sheet-new")
        includeProject(":lib-hidden-api", "hidden-api")
        includeProject(":lib-util", "util")
        includeProject(":lib-api", "api")
        includeProject(":lib-log", "log")
        includeProject(":lib-common", "common")
        includeProject(":lib-compose", "compose")
    }
    projects("test-lib") {
        includeProject(":test-instrument", "instrument")
        includeProject(":test-core", "core")
        includeProject(":test-fake", "fake")
        includeProject(":test-koin", "koin")
    }

    substitutes {
        trySubstitute(Grrfe.std, properties["kotlin-ext.dir"])
        trySubstitute(Grrfe.httpkt, properties["httpkt.dir"])
        trySubstitute(Grrfe.gsonExt, properties["gson-ext.dir"])
        trySubstitute(_1fexd.composeKit, properties["composekit.dir"])
        trySubstitute(LinkSheet.flavors, properties["flavors.dir"])
        trySubstitute("com.github.1fexd.libredirectkt", properties["libredirectkt.dir"]) {
            this["lib"] = "lib"
        }
    }
}

