import java.util.*
import kotlin.experimental.ExperimentalTypeInference

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    plugins {
        kotlin("plugin.serialization") version "2.0.20"
        id("de.fayard.refreshVersions") version "0.60.5"
        id("androidx.navigation.safeargs") version "2.8.2"
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

fun substitute(directory: Any, dependency: String, substitutes: Map<String, String>) {
    includeBuild(directory) {
        dependencySubstitution {
            for ((artifact, project) in substitutes) {
                substitute(module("$dependency:$artifact")).using(project(":$project"))
            }
        }
    }
}

@OptIn(ExperimentalTypeInference::class)
fun Any?.trySubstitute(
    dependency: String,
    @BuilderInference builderAction: MutableMap<String, String>.() -> Unit = {},
) {
    this?.let { substitute(this, dependency, buildMap(builderAction)) }
}


fun hasEnv(name: String): Boolean {
    return System.getenv(name)?.toBooleanStrictOrNull() == true
}


include(":app", ":config")
include(":bottom-sheet")

val isCI = hasEnv("CI")
val isJitPack = hasEnv("JITPACK")

val localProperties = file("local.properties")
val devProperties: Properties? = if (localProperties.exists()) {
    Properties().apply {
        localProperties.reader().use { load(it) }
    }
} else null

val isDev = (devProperties?.get("dev")?.toString()?.toBooleanStrictOrNull() == true)

if (devProperties != null && isDev && (!isCI && !isJitPack)) {
    include(":benchmark")

    devProperties["kotlin-ext.dir"]?.trySubstitute("com.gitlab.grrfe.kotlin-ext") {
        this["core"] = "core"
        this["io"] = "io"
        this["java-time"] = "java-time"
        this["result"] = "result"
    }

    devProperties["gson-ext.dir"].trySubstitute("com.gitlab.grrfe:gson-ext") {
        this["core"] = "core"
    }

    devProperties["android-lifecycle-util.dir"]?.trySubstitute("com.github.1fexd.android-lifecycle-util") {
        this["core"] = "core"
        this["koin"] = "koin"
    }

    devProperties["composekit.dir"]?.trySubstitute("com.github.1fexd.composekit") {
        this["app-core"] = "app-core"
        this["theme-core"] = "theme-core"
        this["theme-preference"] = "theme-preference"
        this["component"] = "component"
        this["core"] = "core"
        this["layout"] = "layout"
    }

    devProperties["android-pref-helper.dir"]?.trySubstitute("com.github.1fexd.android-pref-helper") {
        this["core"] = "core"
        this["compose"] = "compose"
    }

    devProperties["libredirect.dir"]?.trySubstitute("com.github.1fexd:libredirectkt") {
        this[":"] = "lib"
    }

    devProperties["tld-lib.dir"]?.trySubstitute("com.github.1fexd:tld-lib") {
        this[":"] = "lib"
    }

    devProperties["uriparser.dir"]?.trySubstitute("com.github.1fexd:uriparser")
    devProperties["signify.dir"]?.trySubstitute("com.github.1fexd:signifykt")

    devProperties["embed-resolve.dir"]?.trySubstitute("com.github.1fexd:embed-resolve") {
        this[":"] = "core"
    }

    devProperties["clearurl.dir"]?.trySubstitute("com.github.1fexd:clearurlkt")
}

