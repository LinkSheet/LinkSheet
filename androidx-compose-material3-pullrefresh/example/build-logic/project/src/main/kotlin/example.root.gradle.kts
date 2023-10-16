import me.omico.compose.project.internal.UpstreamSynchronizer
import me.omico.compose.project.internal.buildVersions
import me.omico.consensus.dsl.requireRootProject

plugins {
    id("example.gradm")
    id("example.root.git")
    id("example.root.spotless")
}

requireRootProject()

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}

val androidxDirectory: String? = consensus.localProperties.getOrNull<String>("androidx.directory")
    ?.replace("\\:", ":")
    ?.replace("\\\\", "\\")

val syncUpstream by tasks.registering(UpstreamSynchronizer::class) {
    group = "project"
    enabled = androidxDirectory != null
    androidxDirectory?.run {
        versionsProperty.set(buildVersions())
        androidxDirectoryProperty.set(file(androidxDirectory))
        outputSourceDirectoryProperty.set(file("library/src/main/kotlin/androidx/compose/material3/pullrefresh"))
        localPropertiesTemplateFileProperty.set(file("library/local.properties.template"))
    }
}
