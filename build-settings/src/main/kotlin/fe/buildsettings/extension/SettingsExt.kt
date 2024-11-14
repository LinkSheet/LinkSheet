package fe.buildsettings.extension

import org.gradle.api.initialization.Settings
import java.io.File
import java.util.*
import kotlin.experimental.ExperimentalTypeInference


fun Settings.substitute(directory: Any, dependency: String, substitutes: Map<String, String>) {
    includeBuild(directory) { build ->
        build.dependencySubstitution {
            for ((artifact, project) in substitutes) {
                it.substitute(it.module("$dependency:$artifact")).using(it.project(":$project"))
            }
        }
    }
}

@OptIn(ExperimentalTypeInference::class)
fun Settings.trySubstitute(
    dir: Any?,
    dependency: String,
    @BuilderInference builderAction: MutableMap<String, String>.() -> Unit = {},
) {
    dir?.let { substitute(this, dependency, buildMap(builderAction)) }
}


fun hasEnv(name: String): Boolean {
    return System.getenv(name)?.toBooleanStrictOrNull() == true
}

fun File.loadPropertiesOrNull(): Properties? {
    if (!exists()) return null

    return Properties().apply { reader().use { load(it) } }
}

val Settings.isCI: Boolean
    get() = hasEnv("CI")

val Settings.isJitPack: Boolean
    get() = hasEnv("JITPACK")
