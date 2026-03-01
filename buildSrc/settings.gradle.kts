@file:Suppress("UnstableApiUsage")

import com.gitlab.grrfe.gradlebuild.config.GradlePluginPortalRepository
import com.gitlab.grrfe.gradlebuild.config.MavenRepository
import com.gitlab.grrfe.gradlebuild.config.configureRepositories

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
