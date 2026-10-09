@file:Suppress("UnstableApiUsage")

import com.gitlab.grrfe.gradlebuild.config.configureRepositories
import com.gitlab.grrfe.gradlebuild.repository.GradlePluginPortalRepository
import com.gitlab.grrfe.gradlebuild.repository.MavenRepository
import com.gitlab.grrfe.gradlebuild.repository.jitpack
import com.gitlab.grrfe.gradlebuild.repository.mavenCentral

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
    MavenRepository.jitpack(),
    GradlePluginPortalRepository,
    mode = RepositoriesMode.PREFER_SETTINGS
)
