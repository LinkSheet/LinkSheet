import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (!file.exists()) file.createNewFile()
    load(file.inputStream())
}

val androidxDirectory = localProperties["androidx.directory"] as? String

if (androidxDirectory != null && gradle.parent == null) {
    val fetchAndroidx by tasks.registering {
        group = "project"
        exec {
            workingDir = file(androidxDirectory)
            commandLine = listOf("git", "fetch", "--all")
        }
    }
    val checkoutAndroidx by tasks.registering {
        group = "project"
        dependsOn(fetchAndroidx)
        mustRunAfter(fetchAndroidx)
        exec {
            workingDir = file(androidxDirectory)
            commandLine = listOf("git", "checkout", properties["project.androidx.commitId"] as String)
        }
    }
    tasks.register("syncUpstream") {
        group = "project"
        dependsOn(fetchAndroidx, checkoutAndroidx)
        mustRunAfter(fetchAndroidx, checkoutAndroidx)
        doLast {
            val upstream = rootProject.file("$androidxDirectory/compose/material/material/src/commonMain/kotlin/androidx/compose/material/pullrefresh")
            val local = rootProject.file("library/src/main/kotlin/androidx/compose/material3/pullrefresh")
            upstream.walk().forEach { upstreamFile ->
                if (upstreamFile.isDirectory) return@forEach
                val relativePath = upstreamFile.relativeTo(upstream)
                val localFile = local.resolve(relativePath)
                upstreamFile.copyTo(localFile, overwrite = true)
                localFile.readText()
                    .replace(
                        oldValue = "package androidx.compose.material",
                        newValue = "package androidx.compose.material3",
                    )
                    .replace(
                        oldValue = "import androidx.compose.material.ExperimentalMaterialApi\n",
                        newValue = "",
                    )
                    .replace(
                        oldValue = "@ExperimentalMaterialApi\n",
                        newValue = "",
                    )
                    .replace(
                        oldValue = "import androidx.compose.material",
                        newValue = "import androidx.compose.material3",
                    )
                    .replace(
                        oldValue = "MaterialTheme.colors.surface",
                        newValue = "MaterialTheme.colorScheme.surface",
                    )
                    .replace(
                        // For Surface
                        oldValue = "elevation = ",
                        newValue = "shadowElevation = ",
                    )
                    .let(localFile::writeText)
            }
        }
    }
}
