@file:Suppress("UnstableApiUsage")

import com.gitlab.grrfe.gradlebuild.config.configureRepositories
import com.gitlab.grrfe.gradlebuild.repository.*

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
    MavenRepository.mavenCentral(),
    MavenRepository.local(),
    GrrfeRepository.std(),
    MavenRepository.jitpack(),
    GradlePluginPortalRepository,
    mode = RepositoriesMode.PREFER_SETTINGS
)
