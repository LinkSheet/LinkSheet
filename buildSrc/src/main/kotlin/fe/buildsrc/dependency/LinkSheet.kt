package fe.buildsrc.dependency

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation

object LinkSheet : DependencyGroup(group = "com.github.LinkSheet") {
    val flavors = DependencyNotation(group = group, name = "flavors")
    val interconnect = DependencyNotation(group = group, name = "interconnect")
}
