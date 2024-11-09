package fe.buildlogic.dependency

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation
import de.fayard.refreshVersions.core.DependencyNotation.Companion.invoke

object LinkSheet : DependencyGroup(group = "com.github.LinkSheet") {
    val flavors = DependencyNotation(group = group, name = "flavors")
    val interconnect = DependencyNotation(group = group, name = "interconnect")
}
