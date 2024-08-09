import org.gradle.api.initialization.resolve.RepositoriesMode
import java.util.Properties

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
include(":components", ":compose-util", ":bottom-sheet")


val dev = false
if (dev) {
    val properties = Properties().apply {
        file("local.properties").reader().use(::load)
    }

    includeBuild(properties["android-lifecycle-util.dir"].toString())
}

