@file:Suppress("UnstableApiUsage")

rootProject.name = "androidx-compose-material3-pullrefresh"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

        plugins {
            id("com.android.library") version "8.1.0"
            id("com.gradle.enterprise") version "3.13.2"
            kotlin("android") version "1.9.0"
            id("de.fayard.refreshVersions") version "0.60.0"
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") apply false
    id("de.fayard.refreshVersions")
}

if (gradle.parent == null) {
    apply(plugin = "com.gradle.enterprise")
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
            publishAlwaysIf(!gradle.startParameter.isOffline)
        }
    }
}

include("library")
