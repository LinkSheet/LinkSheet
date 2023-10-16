rootProject.name = "example-root"

pluginManagement {
    includeBuild("build-logic/initialization")
}

plugins {
    id("example")
}

include(":app")

includeBuild("../library") {
    dependencySubstitution {
        substitute(module("me.omico.compose:compose-material3-pullrefresh")).using(project(":"))
    }
}
