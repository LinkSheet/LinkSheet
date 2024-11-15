package fe.buildlogic.dependency

import AndroidX
import org.gradle.kotlin.dsl.IsNotADependency


object PinnedVersions : IsNotADependency {
    private const val COMPOSE_VERSION = "1.7.0-beta03"

    var ComposeUi = AndroidX.compose.ui.withVersion(COMPOSE_VERSION)
    var ComposeFoundation = AndroidX.compose.foundation.withVersion(COMPOSE_VERSION)

    var Material3 = AndroidX.compose.material3.withVersion("1.4.0-alpha04")
}
