package fe.buildlogic.dependency

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation
import de.fayard.refreshVersions.core.DependencyNotation.Companion.invoke
import de.fayard.refreshVersions.core.DependencyNotationAndGroup
import org.gradle.kotlin.dsl.IsNotADependency

object Grrfe : DependencyGroup(group = "com.gitlab.grrfe") {
    val httpkt = HttpKt

    object HttpKt : DependencyNotationAndGroup(group = "$group.httpkt", name = "httpkt") {
        val bom = module("platform", isBom = true)

        val core = module("core")
        val gson = module("ext-gson")
    }

    val ext = Ext

    object Ext : IsNotADependency {
        val gson = DependencyNotation(group = group, name = "gson-ext")
    }

    val std = Std

    object Std : DependencyNotationAndGroup(group = "$group.kotlin-ext", name = "kotlin-ext") {
        val bom = module("platform", isBom = true)

        val core = module("core")
        val javaTime = module("java-time")

        val result = Result

        object Result : IsNotADependency {
            val core = DependencyNotation(group = group, name = "result-core")
            val assert = DependencyNotation(group = group, name = "result-assert")
        }

        val process = Process

        object Process : IsNotADependency {
            val core = DependencyNotation(group = group, name = "process-core")
            val android = DependencyNotation(group = group, name = "process-android")
        }

        val uri = DependencyNotation(group = group, name = "uri")
    }
}
