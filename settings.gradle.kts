import fe.buildsettings.extension.*

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    includeBuild("build-settings")

    plugins {
        kotlin("plugin.serialization") version "2.0.20"
        id("de.fayard.refreshVersions") version "0.60.5"
        id("androidx.navigation.safeargs") version "2.8.2"
    }
}

plugins {
    id("de.fayard.refreshVersions")
    id("build-settings-plugin")
}

includeBuild("build-logic")

@Suppress("UnstableApiUsage")
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

val localProperties = file("local.properties")
val devProperties = localProperties.loadPropertiesOrNull()

val isDev = (devProperties?.get("dev")?.toString()?.toBooleanStrictOrNull() == true)

if (devProperties != null && isDev && (!isCI && !isJitPack)) {
    include(":benchmark")

    trySubstituteDir(devProperties["kotlin-ext.dir"], "com.gitlab.grrfe.kotlin-ext") {
        this["core"] = "core"
        this["io"] = "io"
        this["java-time"] = "java-time"
        this["result-core"] = "result:result-core"
        this["result-assert"] = "result:result-assert"
        this["uri"] = "uri"
    }

    trySubstituteDir(devProperties["httpkt.dir"], "com.gitlab.grrfe.httpkt") {
        this["core"] = "core"
        this["ext-gson"] = "ext-gson"
        this["ext-jsoup"] = "ext-jsoup"
    }

    trySubstituteDir(devProperties["gson-ext.dir"], "com.gitlab.grrfe.gson-ext") {
        this["core"] = "core"
    }

    trySubstituteDir(devProperties["android-lifecycle-util.dir"], "com.github.1fexd.android-lifecycle-util") {
        this["core"] = "core"
        this["koin"] = "koin"
    }

    trySubstituteDir(devProperties["android-pref-helper.dir"], "com.github.1fexd.android-pref-helper") {
        this["core"] = "core"
        this["compose"] = "compose"
        this["mock"] = "compose-mock"
    }

    trySubstituteDir(devProperties["composekit.dir"], "com.github.1fexd.composekit") {
        this["app-core"] = "app-core"
        this["theme-core"] = "theme-core"
        this["theme-preference"] = "theme-preference"
        this["component"] = "component"
        this["core"] = "core"
        this["layout"] = "layout"
    }

    trySubstituteDir(devProperties["android-pref-helper.dir"], "com.github.1fexd.android-pref-helper") {
        this["core"] = "core"
        this["compose"] = "compose"
    }

    trySubstituteDir(devProperties["libredirect.dir"], "com.github.1fexd:libredirectkt")

    trySubstituteDir(devProperties["tld-lib.dir"], "com.github.1fexd:tld-lib")
    trySubstituteDir(devProperties["embed-resolve.dir"], "com.github.1fexd:embed-resolve") {
        this[":"] = "core"
    }

    trySubstituteDir(devProperties["clearurl.dir"], "com.github.1fexd:clearurlkt")
}

