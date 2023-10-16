@file:Suppress("UnstableApiUsage")

rootProject.name = "androidx-compose-material3-pullrefresh"

pluginManagement {
    includeBuild("example/build-logic/initialization")
}

plugins {
    id("root")
}

includeBuild("example")
includeBuild("library")
