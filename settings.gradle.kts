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
        kotlin("plugin.serialization")
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
include(":bottom-sheet", ":bottom-sheet-new")
include(":scaffold")
include(":hidden-api")

val localProperties = file("local.properties")
val devProperties = localProperties.loadPropertiesOrNull()

val isDev = (devProperties?.get("dev")?.toString()?.toBooleanStrictOrNull() == true)

if (devProperties != null && isDev && (!isCI && !isJitPack)) {
    include(":benchmark")

    trySubstitute(devProperties["kotlin-ext.dir"], "com.gitlab.grrfe.kotlin-ext") {
        this["core"] = "core"
        this["io"] = "io"
        this["time-core"] = "time:time-core"
        this["time-java"] = "time:time-java"
        this["result-core"] = "result:result-core"
        this["result-assert"] = "result:result-assert"
        this["process-core"] = "process:process-core"
        this["uri"] = "uri"
        this["test"] = "test"
    }

    trySubstitute(devProperties["httpkt.dir"], "com.gitlab.grrfe.httpkt") {
        this["core"] = "core"
        this["serialization-gson"] = "serialization:serialization-gson"
        this["serialization-jsoup"] = "serialization:serialization-jsoup"
    }

    trySubstitute(devProperties["gson-ext.dir"], "com.gitlab.grrfe.gson-ext") {
        this["core"] = "core"
    }

    trySubstitute(devProperties["android-lifecycle-util.dir"], "com.github.1fexd.android-lifecycle-util") {
        this["core"] = "core"
        this["koin"] = "koin"
    }

    trySubstitute(devProperties["android-pref-helper.dir"], "com.github.1fexd.android-pref-helper") {
        this["core"] = "core"
        this["compose"] = "compose:compose-core"
        this["mock"] = "compose:compose-mock"
    }

    trySubstitute(devProperties["android-span-helper.dir"], "com.github.1fexd.android-span-helper") {
        this["core"] = "core"
        this["compose"] = "compose"
    }

    trySubstitute(devProperties["composekit.dir"], "com.github.1fexd.composekit") {
        this["app-core"] = "app:app-core"
        this["theme-core"] = "theme:theme-core"
        this["theme-preference"] = "theme:theme-preference"
        this["component"] = "component"
        this["core"] = "core"
        this["layout"] = "layout"
    }

    trySubstitute(devProperties["libredirect.dir"], "com.github.1fexd:libredirectkt")

    trySubstitute(devProperties["tld-lib.dir"], "com.github.1fexd:tld-lib")
    trySubstitute(devProperties["embed-resolve.dir"], "com.github.1fexd:embed-resolve") {
        this[":"] = "core"
    }

    trySubstitute(devProperties["clearurl.dir"], "com.github.1fexd:clearurlkt")
}

