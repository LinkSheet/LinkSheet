import me.omico.consensus.dsl.requireRootProject
import me.omico.consensus.spotless.ConsensusSpotlessTokens

plugins {
    id("me.omico.consensus.spotless")
}

requireRootProject()

consensus {
    spotless {
        freshmark()
        gradleProperties()
        kotlin(
            targets = setOf("**/src/*/kotlin/**/*.kt"),
            editorConfigOverride = ConsensusSpotlessTokens.Kotlin.editorConfigOverride + mapOf(
                // TODO waiting for ktlint 1.0.1
                // "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                "ktlint_standard_function-naming" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
            ),
        )
        kotlinGradle()
    }
}
