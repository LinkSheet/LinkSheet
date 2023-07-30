@file:Suppress("UnstableApiUsage")

rootProject.name = "androidx-compose-material3-pullrefresh"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal {
            content {
                includeGroupByRegex("com.gradle.*")
            }
        }
        plugins {
            id("com.android.library") version extra["project.android.gradle.plugin.version"] as String
            id("com.gradle.enterprise") version extra["project.gradle.enterprise.plugin.version"] as String
            kotlin("android") version extra["project.kotlin.version"] as String
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
