import me.omico.consensus.dsl.isGitDirectoryExists
import me.omico.consensus.dsl.requireRootProject

plugins {
    id("me.omico.consensus.git")
}

requireRootProject()

consensus {
    git {
        if (!isGitDirectoryExists) return@git
        hooks {
            preCommit {
                appendLine("#!/bin/sh")
                appendLine()
                if (file("gradlew").exists()) {
                    appendLine("# Give gradlew execute permission")
                    appendLine("git ls-files \"*gradlew\" | xargs git update-index --add --chmod=+x")
                    appendLine()
                }
            }
        }
    }
}
