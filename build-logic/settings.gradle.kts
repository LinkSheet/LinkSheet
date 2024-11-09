pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.5"
        kotlin("jvm")
    }
}

plugins {
    id("de.fayard.refreshVersions")
}
