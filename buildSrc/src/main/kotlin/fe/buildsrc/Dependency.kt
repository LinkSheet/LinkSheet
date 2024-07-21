package fe.buildsrc

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation
import de.fayard.refreshVersions.core.DependencyNotationAndGroup
import org.gradle.kotlin.dsl.IsNotADependency

object _1fexd : DependencyGroup(group = "com.github.1fexd") {
    val android = Android

    object Android : IsNotADependency {
        val preference = PreferenceHelper

        object PreferenceHelper : DependencyNotationAndGroup(
            group = "$group.android-pref-helper", name = "android-pref-helper"
        ) {
            val core = module("core")
            val compose = module("compose")
            val composeMock = module("compose-mock")
        }

        val compose = Compose

        object Compose : IsNotADependency {
            val route = DependencyNotation(group = group, name = "compose-route-util")
            val dialog = DependencyNotation(group = group, name = "compose-dialog-helper")
        }

        val span = Span

        object Span : DependencyNotationAndGroup(
            group = "$group.android-span-helper",
            name = "android-span-helper"
        ) {
            val compose = module("compose")
        }
    }

    val uriParser = DependencyNotation(group = group, name = "uriparser")

    val clearUrl = DependencyNotation(group = group, name = "clearurlkt")
    val fastForward = DependencyNotation(group = group, name = "fastforwardkt")
    val libredirectkt = DependencyNotation(group = group, name = "libredirectkt")
    val amp2html = DependencyNotation(group = group, name = "amp2htmlkt")
    val stringBuilder = DependencyNotation(group = group, name = "stringbuilder-util-kt")
    val embedResolve = DependencyNotation(group = group, name = "embed-resolve")
    val signify = DependencyNotation(group = group, name = "signifykt")

}

object Grrfe : DependencyGroup(group = "com.gitlab.grrfe") {
    val httpkt = HttpKt

    object HttpKt : DependencyNotationAndGroup(group = "$group.httpkt", name = "httpkt") {
        val core = module("core")
        val gson = module("ext-gson")
    }

    val ext = Ext

    object Ext : IsNotADependency {
        val gson = DependencyNotation(group = group, name = "gson-ext")
        val kotlin = DependencyNotation(group = group, name = "kotlin-ext")
    }

    val processLauncher = DependencyNotation(group = group, name = "process-launcher")
}

object LinkSheet : DependencyGroup(group = "com.github.LinkSheet") {
    val flavors = DependencyNotation(group = group, name = "flavors")
    val interconnect = DependencyNotation(group = group, name = "interconnect")
}

object PinnedVersions : IsNotADependency {
    private const val COMPOSE_VERSION = "1.7.0-beta03"

    var ComposeUi = AndroidX.compose.ui.withVersion(COMPOSE_VERSION)
    var ComposeFoundation = AndroidX.compose.foundation.withVersion(COMPOSE_VERSION)

    var Material3 = AndroidX.compose.material3.withVersion("1.3.0-beta03")
}

//clearurlkt = "com.github.1fexd:clearurlkt:_"
//fastforwardkt = "com.github.1fexd:fastforwardkt:_"
//libredirectkt = "com.github.1fexd:libredirectkt:_"
//
//mimetypekt = "com.github.1fexd:mimetypekt:_"
//
//amp2htmlkt = "com.github.1fexd:amp2htmlkt:_"
//
//stringbuilder-util-kt = "com.github.1fexd:stringbuilder-util-kt:_"


