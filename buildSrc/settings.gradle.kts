@file:Suppress("UnstableApiUsage")

import fe.buildsettings.config.GradlePluginPortalRepository
import fe.buildsettings.config.MavenRepository
import fe.buildsettings.config.configureRepositories

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    plugins {
        id("de.fayard.refreshVersions") version "0.60.6"
    }
}

plugins {
    id("de.fayard.refreshVersions")
}

configureRepositories(
    MavenRepository.MavenCentral,
    MavenRepository.Jitpack,
    GradlePluginPortalRepository,
    mode = RepositoriesMode.PREFER_SETTINGS
)
