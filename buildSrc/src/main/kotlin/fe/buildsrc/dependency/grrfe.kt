package fe.buildsrc.dependency

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation
import de.fayard.refreshVersions.core.DependencyNotationAndGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Grrfe : DependencyGroup(group = "com.gitlab.grrfe") {
    val httpkt = HttpKt

    object HttpKt : DependencyNotationAndGroup(group = "$group.httpkt", name = "httpkt") {
        val core = module("core")
        val gson = module("ext-gson")
    }

    val ext = Ext

    object Ext : IsNotADependency {
        val gson = DependencyNotation(group = group, name = "gson-ext")
    }

    val std = Std

    object Std : DependencyNotationAndGroup(group = "$group.kotlin-ext", name = "kotlin-ext") {
        val core = module("core")
        val result = module("result")
        val javaTime = module("java-time")
    }

    val processLauncher = DependencyNotation(group = group, name = "process-launcher")
}
