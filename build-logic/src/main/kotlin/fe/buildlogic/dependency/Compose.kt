package fe.buildlogic.dependency

import AndroidX
import org.gradle.kotlin.dsl.IsNotADependency


object PinnedVersions : IsNotADependency {
    const val ComposeVersion = "1.8.0-alpha07"
    const val Material3Version = "1.4.0-alpha05"

    val Material3 = AndroidX.compose.material3.withVersion(Material3Version)
}
