package me.omico.compose.project.internal

import gradle.kotlin.dsl.accessors._9faafd431fc995774dddde80c11e5737.androidx
import gradle.kotlin.dsl.accessors._9faafd431fc995774dddde80c11e5737.versions
import org.gradle.api.Project

internal object Versions {
    const val COMPOSE_BOM = "project.compose.bom.version"
    const val COMPOSE_COMPILER = "project.compose.compiler.version"
    const val COMPOSE_MATERIAL = "project.compose.material.version"
    const val ANDROID_GRADLE_PLUGIN = "project.android.gradle.plugin.version"
    const val KOTLIN = "project.kotlin.version"
}

internal fun Map<String, String>.versionFor(key: String): String =
    getOrElse(key) { error("Property [$key] not found.") }

internal fun Project.buildVersions(): Map<String, String> =
    mapOf(
        Versions.COMPOSE_BOM to versions.androidx.compose.bom,
        Versions.COMPOSE_COMPILER to versions.androidx.compose.compiler,
        Versions.COMPOSE_MATERIAL to dependencies.androidx.compose.material.split(":").last(),
        Versions.ANDROID_GRADLE_PLUGIN to versions.plugins.android,
        Versions.KOTLIN to versions.kotlin,
    )
