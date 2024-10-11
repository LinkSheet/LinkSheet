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
fun Any?.trySubstitute(dependency: String, @BuilderInference builderAction: MutableMap<String, String>.() -> Unit = {}) {
    this?.let { substitute(this, dependency, buildMap(builderAction)) }
}


fun hasEnv(name: String): Boolean {
    return System.getenv(name)?.toBooleanStrictOrNull() == true
}


include(":app", ":config")
include(":bottom-sheet")

val isCI = hasEnv("CI")
val isJitPack = hasEnv("JITPACK")
val dev = false

val substitutes = file("local.properties")
if (dev && (substitutes.exists() && !isCI && !isJitPack)) {
    include(":benchmark")

    val properties = Properties().apply {
        file("local.properties").reader().use { load(it) }
    }

    properties["gson-ext.dir"].trySubstitute("com.gitlab.grrfe:gson-ext") {
        this["core"] = "core"
    }

    properties["android-lifecycle-util.dir"]?.trySubstitute("com.github.1fexd.android-lifecycle-util") {
        this["core"] = "core"
        this["koin"] = "koin"
    }

    properties["composekit.dir"]?.trySubstitute("com.github.1fexd.composekit") {
        this["app-core"] = "app-core"
        this["theme-core"] = "theme-core"
        this["theme-preference"] = "theme-preference"
        this["component"] = "component"
        this["core"] = "core"
        this["layout"] = "layout"
    }

    properties["libredirect.dir"]?.trySubstitute("com.github.1fexd:libredirectkt") {
        this[":"] = "lib"
    }

    properties["tld-lib.dir"]?.trySubstitute("com.github.1fexd:tld-lib") {
        this[":"] = "lib"
    }

    properties["uriparser.dir"]?.trySubstitute("com.github.1fexd:uriparser")
    properties["signify.dir"]?.trySubstitute("com.github.1fexd:signifykt")

    properties["embed-resolve.dir"]?.trySubstitute("com.github.1fexd:embed-resolve") {
        this[":"] = "core"
    }

    properties["clearurl.dir"]?.trySubstitute("com.github.1fexd:clearurlkt")
}

